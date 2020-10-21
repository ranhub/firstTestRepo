package com.ford.turbo.aposb.common.basemodels.hystrix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.*;
import com.ford.turbo.aposb.common.basemodels.utility.HasFordErrorException;
import com.ford.turbo.common.basemodels.controller.exception.*;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.net.ssl.SSLException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BaseCommandExecutionHookTest {

    private BaseCommandExecutionHook executionHook;
    private HystrixInvokable<String> commandInstance;

    @Before
    public void setup() {
        executionHook = new BaseCommandExecutionHook();
        commandInstance = new TestCommandInstance();
    }

    @Test
    public void should_returnException_when_exceptionInstanceOfHasFordError() {
        FordError fordError = new FordError("StatusContext", 1337, "I am a ford error");
        HasFordErrorException exception = new HasFordErrorException(fordError, HttpStatus.I_AM_A_TEAPOT);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION, exception);
        assertThat(ex).isEqualTo(exception);
    }

    @Test
    public void should_returnException_when_exceptionCannotBeHandled() {
        RuntimeException runtimeException = new RuntimeException("I am a runtime exception");
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, runtimeException);
        assertThat(ex).isEqualTo(runtimeException);
    }

    @Test
    public void should_returnCircuitBreakerOpenException_when_failureTypeIsShortCircuit() {
        Exception exception = new Exception("I am an exception");
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.SHORTCIRCUIT, exception);
        assertThat(ex).isInstanceOf(CircuitBreakerOpenException.class);
        CircuitBreakerOpenException circuitBreakerOpenException = (CircuitBreakerOpenException) ex;
        assertThat(circuitBreakerOpenException.getFordError().getMessage()).isEqualTo("TestCommandInstance failed because a backend service currently unavailable (circuit breaker open)");
    }

    @Test
    public void should_returnGatewayTimeoutException_when_failureTypeIsTimeout() {
        Exception exception = new Exception("I am an exception");
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.TIMEOUT, exception);
        assertThat(ex).isInstanceOf(GatewayTimeoutException.class);
        GatewayTimeoutException gatewayTimeoutException = (GatewayTimeoutException) ex;
        assertThat(gatewayTimeoutException.getFordError().getMessage()).isEqualTo("TestCommandInstance timed out");
    }

    @Test
    public void should_returnBadRequestException_when_failureTypeIsBadRequestException() {
        Exception exception = new Exception("I am an exception");
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void should_returnBadRequestException_when_exceptionInstanceOfHystrixBadRequestException() {
        Exception exception = new HystrixBadRequestException("I am a hystrix bad request exception");
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void should_returnBadRequestException_when_exceptionInstanceOf403HttpClientErrorException() {
        Exception exception = new HttpClientErrorException(HttpStatus.FORBIDDEN);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        BadRequestException actualException  = (BadRequestException) ex;
        assertThat(actualException.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(actualException.getFordError().getSourceException()).isEqualTo(HttpClientErrorException.class);
    }
    
    @Test
    public void should_returnBadRequestException_when_exceptionInstanceOf400HttpClientErrorException() {
        Exception exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request" , "ValidationFailed".getBytes(), Charset.defaultCharset());
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        
        BadRequestException actualException  = (BadRequestException) ex;
        assertThat(actualException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actualException.getFordError().getSourceException()).isEqualTo(HttpClientErrorException.class);
        assertThat(actualException.getFordError().getMessage()).isEqualTo("ValidationFailed");
        
    }
    
    @Test
    public void should_returnBadRequestException_when_exceptionInstanceOf400HttpClientErrorExceptionWithBodyBlank() {
        Exception exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request" , "".getBytes(), Charset.defaultCharset());
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        
        BadRequestException actualException  = (BadRequestException) ex;
        assertThat(actualException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actualException.getFordError().getSourceException()).isEqualTo(HttpClientErrorException.class);
        assertThat(actualException.getFordError().getMessage()).isEqualTo("400 Bad Request");
    }

    @Test
    public void should_returnInstanceOfHystrixBadRequestException_when_exceptionInstanceHttpClientErrorException() {
        // The returned exception for 4xx errors must be HystrixBadRequestException otherwise the 4xx errors will
        // affect the failure metrics and can cause the circuit to open
        Exception exception = new HttpClientErrorException(HttpStatus.FORBIDDEN);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(HystrixBadRequestException.class);
        assertThat(((BadRequestException) ex).getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfHttpServerErrorException() {
        Exception exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
        assertThat(ex.getMessage()).isEqualTo("TestCommandInstance threw HttpServerErrorException - 500 INTERNAL_SERVER_ERROR");
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfSoapFaultClientException() {
        Exception exception = mock(SoapFaultClientException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfXmlMappingException() {
        Exception exception = mock(XmlMappingException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfJsonProcessingException() {
        Exception exception = mock(JsonProcessingException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfCertificateException() {
        Exception exception = mock(CertificateException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
    }

    @Test
    public void should_returnBadGatewayException_when_exceptionInstanceOfSSLException() {
        Exception exception = mock(SSLException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(BadGatewayException.class);
    }

    @Test
    public void should_returnGatewayTimeoutException_when_exceptionInstanceOfSocketTimeoutException() {
        Exception exception = mock(SocketTimeoutException.class);
        Exception ex = executionHook.onError(commandInstance, HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, exception);
        assertThat(ex).isInstanceOf(HttpStatusException.class);
        assertThat(((HttpStatusException) ex).getHttpStatus()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    }

    @Test
    public void should_returnTrue_when_hasCause_where_causeInstanceOfClass() {
        Exception exception = mock(SSLException.class);
        assertThat(executionHook.hasCause(exception, SSLException.class)).isTrue();
        RuntimeException runtimeException = new RuntimeException(exception);
        assertThat(executionHook.hasCause(runtimeException, SSLException.class)).isTrue();
    }

    @Test
    public void should_returnFalse_when_hasCause_where_causeNotInstanceOfClass() {
        Exception exception = mock(SSLException.class);
        assertThat(executionHook.hasCause(exception, NullPointerException.class)).isFalse();
        RuntimeException runtimeException = new RuntimeException(exception);
        assertThat(executionHook.hasCause(runtimeException, NullPointerException.class)).isFalse();
    }

    @Test
    public void should_returnSameException_when_findCause_where_causeInstanceOfClass() {
        Exception exception = mock(SSLException.class);
        Throwable cause = executionHook.findCause(exception, SSLException.class);
        assertThat(exception).isEqualTo(cause);

        RuntimeException runtimeException = new RuntimeException(exception);
        cause = executionHook.findCause(runtimeException, SSLException.class);
        assertThat(exception).isEqualTo(cause);
    }

    @Test
    public void should_returnNull_when_findCause_where_causeNotInstanceOfClass() {
        Exception exception = mock(SSLException.class);
        RuntimeException runtimeException = new RuntimeException(exception);
        Throwable cause = executionHook.findCause(runtimeException, NullPointerException.class);
        assertThat(cause).isNull();
    }

    public class TestCommandInstance extends HystrixCommand<String> {

        protected TestCommandInstance() {
            super(HystrixCommandGroupKey.Factory.asKey("Test"));
        }

        @Override
        protected String run() throws Exception {
            return null;
        }
    }

}