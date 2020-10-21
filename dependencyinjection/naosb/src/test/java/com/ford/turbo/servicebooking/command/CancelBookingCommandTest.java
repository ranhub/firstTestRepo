package com.ford.turbo.servicebooking.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingPost;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingResponse;

@RunWith(MockitoJUnitRunner.class)
public class CancelBookingCommandTest {

	private CancelBookingCommand command;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	
	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;
    
	private final String bookingRefNumber = "booking-number";
    private final String baseUrl = "bookings.com";
    private static String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
    
    @Captor
    private ArgumentCaptor<HttpEntity<OSBCCancelBookingPost>> requestCaptor;
	
	@Before
	public void setup(){
		command = new CancelBookingCommand(traceInfo, mutualAuthRestTemplate, bookingRefNumber, baseUrl);
	}
	
	@Test
	public void shouldReturnResponse_andLog() throws Exception{
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				CancelBookingCommand.class);
		
		String responseString = getCancelBookingResponse();
		
		when(mutualAuthRestTemplate.postForEntity(Mockito.contains(baseUrl), any(), eq(String.class)))
			.thenReturn(new ResponseEntity<>(responseString, HttpStatus.OK));
		
		OSBCCancelBookingResponse response = command.doRun();
		
		String logs = capturedLogs.toString();
		
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getData().getIsBookingCancelled()).isEqualTo(true);
		
		verify(mutualAuthRestTemplate).postForEntity(contains(baseUrl), requestCaptor.capture(), eq(String.class));
		
		assertThat(requestCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(requestCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
    	
    	assertThat(logs).containsPattern("DEBUG.*CancelBookingCommand");
    	assertThat(logs).contains("Cancel booking request for " + bookingRefNumber);
    	
    	assertThat(logs).containsPattern("DEBUG.*CancelBookingCommand");
    	assertThat(logs).contains("Response body for booking Id " + bookingRefNumber + ": " + responseString);
	}

	private String getCancelBookingResponse() throws JsonProcessingException {
		OSBCCancelBookingResponse [] cancelBookingResponseArray = new OSBCCancelBookingResponse [1];
		OSBCCancelBookingResponse cancelBookingResponse = new OSBCCancelBookingResponse();
		OSBCCancelBookingData cancelBookingData = new OSBCCancelBookingData();
		cancelBookingData.setIsBookingCancelled(true);
		
		cancelBookingResponse.setData(cancelBookingData);
		cancelBookingResponse.setStatus(200);
		cancelBookingResponseArray[0] = cancelBookingResponse;
		
		String responseString = new ObjectMapper().writeValueAsString(cancelBookingResponseArray);
		return responseString;
	}
}
