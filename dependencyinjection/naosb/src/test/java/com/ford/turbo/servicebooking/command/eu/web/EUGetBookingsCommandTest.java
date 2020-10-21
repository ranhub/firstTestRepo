package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.GetBookingsRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBBookedServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBGetBookingsData;
import com.netflix.config.ConfigurationManager;

@RunWith(MockitoJUnitRunner.class)
public class EUGetBookingsCommandTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	
	private EUGetBookingsCommand command;
	
	private static final String BASE_URL = "http://dealers.com";
	private String accessCode = "accessCode123";
	private String email = "email@domain.com";
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";


	
	@Test
	public void shouldReturnBookings_whenGetBookingsIsInvoked() throws Exception {
		ResponseEntity<EUOSBBookedServicesResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBBookedServicesResponse.class))).thenReturn(responseEntity);
		GetBookingsRequest request = GetBookingsRequest.builder()
										.email(email)
										.accessCode(accessCode)
										.build();
		EUOSBBookedServicesResponse response =  new EUGetBookingsCommand(traceInfo, mockMutualAuthRestTemplate, BASE_URL, request).doRun();
		verify(mockMutualAuthRestTemplate).exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(),
				eq(EUOSBBookedServicesResponse.class));
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
    	
		assertNull(response.getError());
		assertNotNull(response);
		assertNotNull(response.getData());
		
		EUOSBGetBookingsData expected = Utilities.getJsonFileData("retrievedBookingWebResponse.json", EUOSBGetBookingsData.class);
		
		assertThat(response.getData()).isEqualTo(expected);
		assertThat(response.getData().getAppointmentTime()).isEqualTo("15-05-2018T15:30:00");
	}
	
	@Test
	public void shouldLogResponseBody_whenGetBookingsIsInvoked() throws Exception {
		ConfigurationManager.getConfigInstance().setProperty("hystrix.command.EUGetBookingsCommand.execution.isolation.thread.timeoutInMilliseconds", 60000);
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUGetBookingsCommand.class);
		ResponseEntity<EUOSBBookedServicesResponse> responseEntity = createResponseEntity();
		
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBBookedServicesResponse.class))).thenReturn(responseEntity);
		
		GetBookingsRequest request = GetBookingsRequest.builder()
									.email(email)
									.accessCode(accessCode)
									.build();
		
		EUOSBBookedServicesResponse actualResponse =  new EUGetBookingsCommand(traceInfo, mockMutualAuthRestTemplate, BASE_URL, request).doRun();
		
		final String logs = capturedLogs.toString();

		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUGetBookingsCommand .* Response body:.*dealerProfile");
	}
	
	@Test
	public void shouldReturn_baseUrl_calling_constructBaseURL(){
		GetBookingsRequest request = GetBookingsRequest.builder()
				.email(email)
				.accessCode(accessCode)
				.build();
		command = new EUGetBookingsCommand(traceInfo, mockMutualAuthRestTemplate, BASE_URL, request);
		String actualBaseUrl = command.constructBaseUrl();
		assertThat(actualBaseUrl).isEqualTo("http://dealers.com/rest/v1/booking?email=email@domain.com&accessCode=accessCode123");
	}
	
	private ResponseEntity<EUOSBBookedServicesResponse> createResponseEntity() {
		EUOSBBookedServicesResponse osbResponse = EUOSBBookedServicesResponse.builder().build();
		EUOSBGetBookingsData responseData = Utilities.getJsonFileData("retrievedBookingWebResponse.json", EUOSBGetBookingsData.class);
		osbResponse.setData(responseData);
		ResponseEntity<EUOSBBookedServicesResponse> responseEntity = ResponseEntity.ok(osbResponse);
		return responseEntity;
	}
	
	
}
