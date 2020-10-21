package com.ford.turbo.aposb.common.authsupport.vehicles;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import com.ford.turbo.aposb.common.basemodels.model.CSDNError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

@RunWith(MockitoJUnitRunner.class)
public class GetVehicleDetailsFromCSDNCommandTest {
	@Mock
	private RestTemplate restTemplate;

	private ByteArrayOutputStream capturedLogs;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
	
	@Before
	public void setUp() throws Exception {
		capturedLogs = given_requestContentsBeingLogged();
	}

	@Test
	public void shouldReturnVehicleDetails_WhenCSDNGetUsersIsCalled() {
		
		String guid = "9685f635-2c76-4587-bcb0-eef85a1dc954";
		String appId = "0599950B-66AD-4F9D-BA69-E3608501D184";
		String token = "csdnToken";
		String vin = "vin";
		
		CredentialsSource credentialsCSDN = Mockito.mock(CredentialsSource.class);
		Mockito.when(credentialsCSDN.getBaseUri()).thenReturn("http://abc");
		
		CommonSDNUserVehicleResponse response = new CommonSDNUserVehicleResponse();
		CSDNVehicle vehicle = new CSDNVehicle();
		vehicle.setVin("validVIN");
		response.setVehicle(vehicle);
		
		Mockito.when(restTemplate.exchange(Mockito.startsWith("http://abc"),Mockito.eq(HttpMethod.GET),Mockito.any(HttpEntity.class),Mockito.eq(CommonSDNUserVehicleResponse.class)))
		.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		
		GetVehicleDetailsFromCSDNCommand command = new GetVehicleDetailsFromCSDNCommand(Mockito.mock(TraceInfo.class,Mockito.RETURNS_DEEP_STUBS),
				restTemplate, vin, token, appId, guid, credentialsCSDN);
		CommonSDNUserVehicleResponse actualResult = command.execute();
		Assert.assertEquals("validVIN", actualResult.getVehicle().getVin());
	}
	
	@Test
	public void shouldLogError_whenVehicleDetailsResponseHasError(){
		String guid = "9685f635-2c76-4587-bcb0-eef85a1dc954";
		String appId = "0599950B-66AD-4F9D-BA69-E3608501D184";
		String token = "csdnToken";
		String vin = "vin";
		
		CredentialsSource credentialsCSDN = Mockito.mock(CredentialsSource.class);
		Mockito.when(credentialsCSDN.getBaseUri()).thenReturn("http://abc");
		
		CommonSDNUserVehicleResponse response = CommonSDNUserVehicleResponse.builder()
		.vehicle(CSDNVehicle.builder().vin("validVIN").build())
		.error(CSDNError.builder()
				  .message("Mock Error Message")
				  .statusCode("MockStatusCode")
				  .statusContext("MockStatusContext")
				  .build())
		.build();
		
		Mockito.when(restTemplate.exchange(Mockito.startsWith("http://abc"),Mockito.eq(HttpMethod.GET),Mockito.any(HttpEntity.class),Mockito.eq(CommonSDNUserVehicleResponse.class)))
		.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		
		GetVehicleDetailsFromCSDNCommand command = new GetVehicleDetailsFromCSDNCommand(Mockito.mock(TraceInfo.class,Mockito.RETURNS_DEEP_STUBS),
				restTemplate, vin, token, appId, guid, credentialsCSDN);
		CommonSDNUserVehicleResponse actualResult = command.execute();
		Assert.assertEquals("validVIN", actualResult.getVehicle().getVin());
		
		String logs = capturedLogs.toString();
		assertThat(logs).contains("Mock Error Message");
		assertThat(logs).contains("MockStatusCode");
		assertThat(logs).contains("MockStatusContext");
	}
	
	protected ByteArrayOutputStream given_requestContentsBeingLogged() {
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
        ((Logger) LoggerFactory.getLogger(GetVehicleDetailsFromCSDNCommand.class)).setLevel(Level.INFO);

        return capturedLogs;
    }

}
