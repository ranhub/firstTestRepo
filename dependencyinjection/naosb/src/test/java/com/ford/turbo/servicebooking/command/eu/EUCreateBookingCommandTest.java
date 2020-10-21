package com.ford.turbo.servicebooking.command.eu;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
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

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;

@RunWith(MockitoJUnitRunner.class)
public class EUCreateBookingCommandTest {
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	private String osbUrl = "/rest/v1/booking";
	private String baseUrl = "http://apples.com/bananas/create";
	@Captor
	private ArgumentCaptor<HttpEntity<CreateBookingWebRequest>> httpEntityCaptor;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	@Test
	public void shouldExtendTimedHystrixCommand() {
		
		assertThat((TimedHystrixCommand.class).isAssignableFrom(EUCreateBookingCommand.class)).isTrue();
	}
	
	@Test
	public void shouldReturn_euOSBCreateBookingResponse() {
		
		CreateBookingOSBRequest request = createRequest();
		EUCreateBookingCommand command = new EUCreateBookingCommand(mockTraceInfo,
				mockMutualAuthRestTemplate, baseUrl, request);
		ResponseEntity<EUOSBCreateBookingResponse> responseEntity = createMockOsbResponse();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(EUOSBCreateBookingResponse.class)))
			.thenReturn(responseEntity);
		EUOSBCreateBookingResponse response = command.execute();
		
		assertNotNull(response);
		assertNotNull(response.getData());
		assertThat(response.getData().getAccessCode()).isEqualTo("my-access-code");
		assertThat(response.getData().getBookingReferenceNumber()).isEqualTo("my-booking-ref");
		assertNull(response.getError());
		verify(mockMutualAuthRestTemplate).exchange(contains(baseUrl + osbUrl), eq(HttpMethod.POST), httpEntityCaptor.capture(),
				eq(EUOSBCreateBookingResponse.class));
		assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(request);
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
    		assertThat(command.getCommandGroup().toString()).isEqualTo("PUBLIC_EU_GROUP");
	}
	
	@Test
	public void shouldLogRequest_andResponse_forVin() {
		
		CreateBookingOSBRequest request = createRequest();
		request.setVin("12345678901234567");
		String requestMessage = "create booking by vin: " + request.getVin();
		assertRequestAndResponseLogs(request, requestMessage);
	}

	@Test
	public void shouldLogRequest_andResponse_forRegistrationNumber() {
		
		CreateBookingOSBRequest request = createRequest();
		request.setRegistrationNumber("QWER123");
		String requestMessage = "create booking by registration number: " + request.getRegistrationNumber();
		assertRequestAndResponseLogs(request, requestMessage);
	}

	@Test
	public void shouldLogRequest_andResponse_forModelName_andBuildYear() {
		
		CreateBookingOSBRequest request = createRequest();
		request.setModelName("Fiesta");
		request.setBuildYear("2018");
		String requestMessage = "create booking by model name and build year: " + request.getModelName() + " " + request.getBuildYear();
		assertRequestAndResponseLogs(request, requestMessage);
	}

	private ResponseEntity<EUOSBCreateBookingResponse> createMockOsbResponse() {
		
		EUOSBCreateBookingResponse osbResponse = EUOSBCreateBookingResponse.builder().build();
		CreateBookingWebWrapper wrapper = CreateBookingWebWrapper.builder().build();
		osbResponse.setData(wrapper);
		wrapper.setAccessCode("my-access-code");
		wrapper.setBookingReferenceNumber("my-booking-ref");
		ResponseEntity<EUOSBCreateBookingResponse> responseEntity = ResponseEntity.ok(osbResponse);
		return responseEntity;
	}
	
	private CreateBookingOSBRequest createRequest() {
		
		CreateBookingOSBRequest request = CreateBookingOSBRequest.builder().build();
		request.setMarketCode("GBR");
		return request; 
	}
	
	private void assertRequestAndResponseLogs(CreateBookingOSBRequest request, String requestMessage) {
		
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUCreateBookingCommand.class);
		EUCreateBookingCommand command = new EUCreateBookingCommand(mockTraceInfo,
				mockMutualAuthRestTemplate, baseUrl, request);
		ResponseEntity<EUOSBCreateBookingResponse> responseEntity = createMockOsbResponse();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(EUOSBCreateBookingResponse.class)))
		.thenReturn(responseEntity);
		EUOSBCreateBookingResponse response = command.execute();
		final String logs = capturedLogs.toString();
		
		assertThat(response).isNotNull();
		assertThat(logs)
		.contains(requestMessage);
		assertThat(logs).containsPattern("INFO .*EUCreateBookingCommand *: created booking reference number: "
				+ response.getData().getBookingReferenceNumber());
	}
}
