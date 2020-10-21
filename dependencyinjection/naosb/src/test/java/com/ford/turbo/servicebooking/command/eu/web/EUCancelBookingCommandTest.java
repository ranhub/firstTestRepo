
package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.CancelBookingRequest;
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

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponseData;

@RunWith(MockitoJUnitRunner.class)
public class EUCancelBookingCommandTest {
	
	private static final String BASE_URL = "http://dealers.com";
	
	private final String cancelBookingUrl = "/rest/v1/booking?bookingReferenceNumber=%s&accessCode=%s&osbSiteTermsRequired=%s";
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	private String bookingReferenceNumber = "bookingRef1234";
	private String accessCode = "accessCode123";
	private boolean osbSiteTermsRequired = true;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	@Test
	public void should_extendTimedHystrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUCancelBookingCommand.class)).isTrue();
	}

	@Test
	public void shouldReturn_cancelBooking() {
		ResponseEntity<EUOSBCancelBookingResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(EUOSBCancelBookingResponse.class))).thenReturn(responseEntity);
		CancelBookingRequest request = createCancelBookingRequest();
		EUCancelBookingCommand command = getCommand(request);
		EUOSBCancelBookingResponse response = command.execute();
		
		assertNotNull(response);
		assertNotNull(response.getData());
		assertTrue(response.getData().getIsBookingCancelled());
		assertNull(response.getError());
		String osbCancelBookingUrl = getOsbCancelBookingUrl(request);
		verify(mockMutualAuthRestTemplate).exchange(contains(osbCancelBookingUrl), eq(HttpMethod.DELETE), httpEntityCaptor.capture(),
				eq(EUOSBCancelBookingResponse.class));
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}

	@Test
	public void should_logResponseBody() throws Exception {
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUCancelBookingCommand.class);
		ResponseEntity<EUOSBCancelBookingResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(EUOSBCancelBookingResponse.class))).thenReturn(responseEntity);
		CancelBookingRequest request = createCancelBookingRequest();
		EUOSBCancelBookingResponse actualResponse = getCommand(request).execute();
		final String logs = capturedLogs.toString();

		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUCancelBookingCommand *: Response body*");
	}

	protected String getOsbCancelBookingUrl(CancelBookingRequest request) {
		String osbCancelBookingUrl = String.format(cancelBookingUrl, request.getBookingReferenceNumber(), request.getAccessCode(), request.isOsbSiteTermsRequired());
		return osbCancelBookingUrl;
	}
	
	private EUCancelBookingCommand getCommand(CancelBookingRequest request) {
		
		return new EUCancelBookingCommand(traceInfo, mockMutualAuthRestTemplate, BASE_URL, request);
	}

	private ResponseEntity<EUOSBCancelBookingResponse> createResponseEntity() {
		
		EUOSBCancelBookingResponse osbResponse = EUOSBCancelBookingResponse.builder().build();
		EUOSBCancelBookingResponseData responseData = EUOSBCancelBookingResponseData.builder().isBookingCancelled(true).build();
		osbResponse.setData(responseData);
		ResponseEntity<EUOSBCancelBookingResponse> responseEntity = ResponseEntity.ok(osbResponse);
		return responseEntity;
	}

	private CancelBookingRequest createCancelBookingRequest() {
		
		return CancelBookingRequest.builder()
				.bookingReferenceNumber(bookingReferenceNumber)
				.accessCode(accessCode)
				.osbSiteTermsRequired(osbSiteTermsRequired)
				.build();
	}
}
