package com.ford.turbo.servicebooking.command.eu.web;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleFeaturesResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

@RunWith(MockitoJUnitRunner.class)
public class EUWebVehicleFeaturesCommandTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;

	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;

	@Mock
	private CredentialsSource osbCredentials;

	private EUWebVehicleFeaturesRequest request;

	private final String BASE_URL = "/api/vehicleFeatures";

	private String locale;
	private String consoleLoggingPattern;
	private String marketCode;

	private ObjectMapper mapper = new ObjectMapper();
	
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	
	@Before
	public void before() {
		when(osbCredentials.getBaseUri()).thenReturn(BASE_URL);
		consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
	}

	@Test
	public void should_extendTimedHystrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUWebVehicleFeaturesCommand.class)).isTrue();
	}

	@Test
	public void should_returnVehicleFeaturesList() throws Exception {

		createEUWebVehicleFeaturesRequest();
		ResponseEntity<EUOSBVehicleFeaturesResponse> responseEntity = createResponseEntity();

		when(mockMutualAuthRestTemplate.exchange(contains("/rest/v1/vehicle/vehicleData"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBVehicleFeaturesResponse.class))).thenReturn(responseEntity);

		EUWebVehicleFeaturesCommand command = new EUWebVehicleFeaturesCommand(traceInfo, mockMutualAuthRestTemplate,
				osbCredentials.getBaseUri(), request);
		EUOSBVehicleFeaturesResponse response = command.execute();
		
		assertThat(response).isNotNull();
		assertThat(response.getData()).isNotNull();
		assertThat(response.getData().getModel().get(0)).isEqualTo("B-MAX");
		assertThat(response.getData().getModel().get(1)).isEqualTo("EcoSport");
		assertThat(response.getData().getModelYear().get(0)).isEqualTo("2017");
		assertThat(response.getData().getModelYear().get(1)).isEqualTo("2016");
		assertThat(response.getError()).isNull();
		
		Mockito.verify(mockMutualAuthRestTemplate).exchange(contains("/rest/v1/vehicle/vehicleData"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(EUOSBVehicleFeaturesResponse.class));
		
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}

	@Test
	public void should_logResponseHeaders() throws Exception {
		final ByteArrayOutputStream capturedLogs = given_requestContentsBeingLogged();
		
		createEUWebVehicleFeaturesRequest();
		ResponseEntity<EUOSBVehicleFeaturesResponse> responseEntity = createResponseEntity();

		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBVehicleFeaturesResponse.class))).thenReturn(responseEntity);

		EUWebVehicleFeaturesCommand command = new EUWebVehicleFeaturesCommand(traceInfo, mockMutualAuthRestTemplate,
				osbCredentials.getBaseUri(), request);
		command.execute();

		final String logs = capturedLogs.toString();
		assertThat(logs).containsPattern("DEBUG .*EUWebVehicleFeaturesCommand *: Response body");
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
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.ERROR);
		((Logger) LoggerFactory.getLogger(EUWebVehicleFeaturesCommand.class)).setLevel(Level.DEBUG);

		return capturedLogs;
	}

	private ResponseEntity<EUOSBVehicleFeaturesResponse> createResponseEntity()
			throws UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {
		Resource resource = new ClassPathResource("euVehicleFeaturesSuccessfulResponse.json");
		String euVehicleFeaturesResponse = new String(readAllBytes(resource.getFile().toPath()), "UTF8");
		
		ResponseEntity<EUOSBVehicleFeaturesResponse> responseEntity = ResponseEntity.ok(mapper.readValue(euVehicleFeaturesResponse, EUOSBVehicleFeaturesResponse.class));
		return responseEntity;
	}
	
	private void createEUWebVehicleFeaturesRequest(){
		locale = "en-GB";
		marketCode = "GBR";	
		request = new EUWebVehicleFeaturesRequest(locale, marketCode);
		
	}
}
