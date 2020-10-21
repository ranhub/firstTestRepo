
package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
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
import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
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

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponseData;

@RunWith(MockitoJUnitRunner.class)
public class EUAccessCodesNotificationCommandTest {

	private static final String BASE_URL = "http://dealers.com";
	private final String accessCodeNotificationUrl = "/rest/v1/booking/forgottenAccessCode?marketCode=%s&email=%s&osbSiteTermsRequired=%s";
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	@Test
	public void should_extendTimedHystrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUAccessCodesNotificationCommand.class)).isTrue();
	}

	@Test
	public void shouldReturn_accessCodeNotification() {
		ResponseEntity<EUOSBAccessCodesNotificationResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBAccessCodesNotificationResponse.class))).thenReturn(responseEntity);
		AccessCodesNotificationRequest request = createAccessCodeNotificationRequest();
		EUOSBAccessCodesNotificationResponse response = getCommand(request).execute();

		assertNotNull(response);
		assertNotNull(response.getData());
		assertTrue(response.getData().getIsReminderSent());
		String osbAccessCodeNotificationUrl = getOsbAccessCodeNotificationUrl(request);
		verify(mockMutualAuthRestTemplate).exchange(contains(osbAccessCodeNotificationUrl), eq(HttpMethod.GET),
				httpEntityCaptor.capture(), eq(EUOSBAccessCodesNotificationResponse.class));
		
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}

	@Test
	public void should_logResponseBodyIfDebugEnabled() throws Exception {
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUAccessCodesNotificationCommand.class);
		ResponseEntity<EUOSBAccessCodesNotificationResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBAccessCodesNotificationResponse.class))).thenReturn(responseEntity);
		AccessCodesNotificationRequest request = createAccessCodeNotificationRequest();
		EUOSBAccessCodesNotificationResponse actualResponse = getCommand(request).execute();
		final String logs = capturedLogs.toString();

		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUAccessCodesNotificationCommand *: Response body*");
	}
	
	private AccessCodesNotificationRequest createAccessCodeNotificationRequest() {
		AccessCodesNotificationRequest request = AccessCodesNotificationRequest.builder()
				.marketCode("GBR")
				.email("henry@ford.com")
				.osbSiteTermsRequired(true)
				.build();
		
		return request;
	}

	private String getOsbAccessCodeNotificationUrl(AccessCodesNotificationRequest request) {
		String url = BASE_URL + String.format(accessCodeNotificationUrl, request.getMarketCode(), request.getEmail(),
				request.getOsbSiteTermsRequired());
		return url;
	}
	
	private ResponseEntity<EUOSBAccessCodesNotificationResponse> createResponseEntity() {

		EUOSBAccessCodesNotificationResponse expectedResponse = EUOSBAccessCodesNotificationResponse.builder().build();
		expectedResponse.setData(EUOSBAccessCodesNotificationResponseData.builder().isReminderSent(true).build());
		ResponseEntity<EUOSBAccessCodesNotificationResponse> responseEntity = ResponseEntity.ok(expectedResponse);
		return responseEntity;
	}

	private EUAccessCodesNotificationCommand getCommand(AccessCodesNotificationRequest request) {
		return new EUAccessCodesNotificationCommand(mockTraceInfo, mockMutualAuthRestTemplate, BASE_URL, request);
	}
}
