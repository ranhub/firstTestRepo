package com.ford.turbo.aposb.common.basemodels.controller.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.utility.HasFordErrorException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

public class ControllerExceptionHandlerTest {

	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	private ControllerExceptionHandler exceptionHandler;

	@Before
	public void setup(){
		exceptionHandler = new ControllerExceptionHandler();
	}

	@Test
	public void should_return500_when_exception_notInstanceOf_HasFordError() {
		ResponseEntity<?> responseEntity = exceptionHandler.handleUncaughtException(new RuntimeException("I am not a FordError"));
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		BaseResponse baseResponse = (BaseResponse) responseEntity.getBody();
		assertThat(baseResponse.getRequestStatus()).isEqualTo(BaseResponse.RequestStatus.UNAVAILABLE);
		assertThat(baseResponse.getError().getStatusContext()).isEqualTo("HTTP");
		assertThat(baseResponse.getError().getStatusCode()).isEqualTo(500);
		assertThat(baseResponse.getError().getMessage()).isEqualTo("I am not a FordError");
	}

	@Test
	public void should_returnHttpStatusFromFordError_when_exception_instanceOf_HasFordError() {
		FordError fordError = new FordError("AnyStatusContext", 9001, "It's over 9000!");
		ResponseEntity<?> responseEntity = exceptionHandler.handleUncaughtException(new HasFordErrorException(fordError, HttpStatus.I_AM_A_TEAPOT));
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT);
		BaseResponse baseResponse = (BaseResponse) responseEntity.getBody();
		assertThat(baseResponse.getRequestStatus()).isEqualTo(BaseResponse.RequestStatus.UNAVAILABLE);
		assertThat(baseResponse.getError().getStatusContext()).isEqualTo("AnyStatusContext");
		assertThat(baseResponse.getError().getStatusCode()).isEqualTo(9001);
		assertThat(baseResponse.getError().getMessage()).isEqualTo("It's over 9000!");
	}

	@Test
	public void should_returnHttpStatusFromFordError_when_cause_instanceOf_HasFordError() {
		FordError fordError = new FordError("AnyStatusContext", 9001, "It's over 9000!");
		RuntimeException runtimeException = new RuntimeException(new HasFordErrorException(fordError, HttpStatus.I_AM_A_TEAPOT));
		ResponseEntity<?> responseEntity = exceptionHandler.handleUncaughtException(runtimeException);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT);
		BaseResponse baseResponse = (BaseResponse) responseEntity.getBody();
		assertThat(baseResponse.getRequestStatus()).isEqualTo(BaseResponse.RequestStatus.UNAVAILABLE);
		assertThat(baseResponse.getError().getStatusContext()).isEqualTo("AnyStatusContext");
		assertThat(baseResponse.getError().getStatusCode()).isEqualTo(9001);
		assertThat(baseResponse.getError().getMessage()).isEqualTo("It's over 9000!");
	}

	@Test
	public void should_NotStacktrace_when_cause_instanceOf_IsSkippedForLogging() {

		ByteArrayOutputStream capturedLogs = given_requestContentsBeingLogged();
		
		Exception exception = new InvalidVinException("1234567890");
		exceptionHandler.handleUncaughtException(exception);

		exception = new AppIdNotFoundException();
		exceptionHandler.handleUncaughtException(exception);

		exception = new AuthTokenExpiredException();
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new AuthTokenFailedException();
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new NoBackendConfiguredException();
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new NoBackendAvailableException();
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new AuthTokenNotFoundException();
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new AuthTokenNotAuthorizedException("Auth token not authorized", new Exception());
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new AuthTokenMalformedException();
		exceptionHandler.handleUncaughtException(exception);
		
		TimedHystrixCommand<Object> hystrixInvokable = mock(TimedHystrixCommand.class);
		Exception causeException = new Exception();
		exception = new CircuitBreakerOpenException(hystrixInvokable, causeException);
		exceptionHandler.handleUncaughtException(exception);

		exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		exceptionHandler.handleUncaughtException(exception);

		final String logs = capturedLogs.toString();

		assertThat(logs).contains("FordError:InvalidVinException VIN (1234567890) must be 17 uppercase alphanumeric characters with source:InvalidVinException");
		assertThat(logs).contains("FordError:AppIdNotFoundException Authorization has been denied for this request. App Id could be missing. with source:AppIdNotFoundException");
		assertThat(logs).contains("FordError:AuthTokenExpiredException Expired Auth Token with source:AuthTokenExpiredException");
		assertThat(logs).contains("FordError:AuthTokenFailedException Malformed Token with source:AuthTokenFailedException");
		assertThat(logs).contains("FordError:NoBackendConfiguredException Backend configuration is currently not configured for this endpoint with source:NoBackendConfiguredException");
		assertThat(logs).contains("FordError:NoBackendAvailableException Service is not configured for the request Application ID with source:NoBackendAvailableException");
		assertThat(logs).contains("FordError:AuthTokenNotFoundException Authorization has been denied for this request. Token could be missing. with source:AuthTokenNotFoundException");
		assertThat(logs).contains("FordError:AuthTokenNotAuthorizedException Auth token not authorized with source:AuthTokenNotAuthorizedException");
		assertThat(logs).contains("FordError:AuthTokenMalformedException Auth token is malformed with source:AuthTokenMalformedException");
	}
	
	@Test
	public void should_notStackTrace_when_cause_isInstanceOf_hasFordError_andMSL_Context() {

		ByteArrayOutputStream capturedLogs = given_requestContentsBeingLogged();

		FordError error = new FordError("Marketing Services Layer", HttpStatus.BAD_REQUEST.value(), "Request is not up to the mark Returning ford error");
		Exception exception = new BadRequestException(error);

		exceptionHandler.handleUncaughtException(exception);
		
		exception = new BadRequestException(new NullPointerException("Null Pointer"),"Marketing Services Layer");
		
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new NoBackendConfiguredException();
		
		exceptionHandler.handleUncaughtException(exception);
		
		exception = new NoBackendAvailableException();
		
		exceptionHandler.handleUncaughtException(exception);
		
		HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		exception = new BadRequestException(clientErrorException, clientErrorException.getStatusCode());
		exceptionHandler.handleUncaughtException(exception);
		
		HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		exception = new BadRequestException(serverErrorException, serverErrorException.getStatusCode());
		exceptionHandler.handleUncaughtException(exception);
		
		final String logs = capturedLogs.toString();

		assertThat(logs).contains("FordError:BadRequestException Request is not up to the mark Returning ford error with source:BadRequestException");
		assertThat(logs).contains("FordError:BadRequestException Null Pointer with source:NullPointerException");
		assertThat(logs).contains("FordError:NoBackendConfiguredException Backend configuration is currently not configured for this endpoint with source:NoBackendConfiguredException");
		assertThat(logs).contains("FordError:NoBackendAvailableException Service is not configured for the request Application ID with source:NoBackendAvailableException");
	}

	@Test
	public void test_InvalidVin() {
		InvalidVinException invalidVinException = new InvalidVinException(new FordError("msl-vinlookup-service-PTS", 500, "reason"));
		boolean whiteListedException = exceptionHandler.skipLoggingException(invalidVinException);
		
		assertTrue(whiteListedException);
	}
	
	@Test
	public void test_MissingParamterException() {
		MissingServletRequestParameterException exception = new MissingServletRequestParameterException("param1", "String");
		boolean whiteListedException = exceptionHandler.skipLoggingException(exception);
		
		assertTrue(whiteListedException);
	}

	private ByteArrayOutputStream given_requestContentsBeingLogged() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(consoleLoggingPattern);
		ple.setContext(lc);
		ple.start();

		ByteArrayOutputStream capturedLogs = new ByteArrayOutputStream();
		OutputStreamAppender<ILoggingEvent> logAppender = new OutputStreamAppender<>();
		logAppender.setEncoder(ple);
		logAppender.setContext(lc);
		logAppender.setOutputStream(capturedLogs);
		logAppender.start();

		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(logAppender);
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

		return capturedLogs;
	}

}