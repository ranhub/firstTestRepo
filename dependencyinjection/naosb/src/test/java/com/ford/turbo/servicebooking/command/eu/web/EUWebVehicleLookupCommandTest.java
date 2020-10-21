package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleDetails;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleLookupRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleLookupResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

@RunWith(MockitoJUnitRunner.class)
public class EUWebVehicleLookupCommandTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TraceInfo mockTraceInfo;

	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	
	private EUWebVehicleLookupCommand command;

	private ByteArrayOutputStream capturedLogs;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	String vin = "12345678901234567";
	String registrationNumber = "REG 1234";
	String locale = "en-gb";
	String marketCode = "GBR";
	long mileage = 1000000;
	String ecatMarketCode = "GB";
	boolean osbSiteTermsRequired = true;
	private static String url = "http://osbBaseUrl/rest/v1/vehicle/vehicleDetails";
	
	@Captor
	private ArgumentCaptor<HttpEntity<EUWebVehicleLookupRequest>> httpEntityCaptor;

	@Before
	public void setup() {
		command = new EUWebVehicleLookupCommand(mockTraceInfo, mockMutualAuthRestTemplate, "http://osbBaseUrl", getVehicleLookupRequestObject());
		
		capturedLogs = given_requestContentsBeingLogged();
	}

	@Test
	public void  shouldReturnVechileDetailsResponse() throws Exception {
		EUOSBVehicleLookupResponse exptectedResponse = getEUOSBVehicleLookupResponseObject();
		when(mockMutualAuthRestTemplate.postForEntity(eq(url), any(), eq(EUOSBVehicleLookupResponse.class)))
			.thenReturn(ResponseEntity.ok(exptectedResponse));

		EUOSBVehicleLookupResponse actualResponse = command.doRun();

		String logs = capturedLogs.toString();
		verify(mockMutualAuthRestTemplate).postForEntity(eq(url), httpEntityCaptor.capture(), eq(EUOSBVehicleLookupResponse.class));
		assertThat(httpEntityCaptor.getValue().getBody().getVin()).isEqualTo(vin);
		assertEquals(exptectedResponse, actualResponse);
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
		
		assertThat(logs).contains("Request body: EUWebVehicleLookupRequest(vin=12345678901234567, registrationNumber=REG 1234, locale=en-gb, marketCode=GBR, mileage=1000000, ecatMarketCode=GB, osbSiteTermsRequired=true)");
		assertThat(logs).contains("Response body: EUOSBVehicleLookupResponse(vehicleDetails=EUWebVehicleDetails(engine=engine");
	}

	@Test
	public void should_logExecutionTime(){
		EUOSBVehicleLookupResponse exptectedResponse = getEUOSBVehicleLookupResponseObject();
		when(mockMutualAuthRestTemplate.postForEntity(eq(url), any(), eq(EUOSBVehicleLookupResponse.class)))
			.thenReturn(ResponseEntity.ok(exptectedResponse));

		command.execute();

		String logs = capturedLogs.toString();
		assertThat(logs).containsPattern("Execution time: command=EUWebVehicleLookupCommand, groupKey=PUBLIC_EU_GROUP, time=[0-9]* ms");
	}

	private EUOSBVehicleLookupResponse getEUOSBVehicleLookupResponseObject() {
		EUOSBVehicleLookupResponse reponse = EUOSBVehicleLookupResponse.builder().build();
		reponse.setVehicleDetails(getEUOSBVehicleDetailsObject());
		return reponse;
	}

	private EUWebVehicleDetails getEUOSBVehicleDetailsObject() {
		return EUWebVehicleDetails
				.builder()
				.vin("vin")
				.vehicleLineCode("vehicleLineCode")
				.version("version")
				.bodyStyle("bodyStyle")
				.buildDate("buildDate")
				.color("color")
				.engine("engine")
				.fuelType("fuelType")
				.mileageInKm("mileageInKm")
				.mileageInMiles("mileageInMiles")
				.registrationNumber("registrationNumber")
				.transmission("transmission")
				.transmissionType("transmissionType")
				.modelName("modelName")
				.build();
	}

	private EUWebVehicleLookupRequest getVehicleLookupRequestObject(){
		EUWebVehicleLookupRequest vehicleLookupRequest = 
				EUWebVehicleLookupRequest.builder()
				.vin(vin)
				.registrationNumber(registrationNumber)
				.locale(locale).marketCode(marketCode)
				.mileage(mileage)
				.ecatMarketCode(ecatMarketCode)
				.osbSiteTermsRequired(osbSiteTermsRequired)
				.build();
		return vehicleLookupRequest;
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
		((Logger) LoggerFactory.getLogger(EUWebVehicleLookupCommand.class)).setLevel(Level.DEBUG);
		((Logger) LoggerFactory.getLogger(TimedHystrixCommand.class)).setLevel(Level.DEBUG);

		return capturedLogs;
	}

}