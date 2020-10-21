
package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerCalendarRequest;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.commons.lang.StringUtils;
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

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerCalendarResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUWebDealerCalendarCommandTest {

	private static final String BASE_URL = "http://dealers.com";
	private static final String EU_OSB_DEALER_CALENDAR_MANDATORY_PARAMS_PATH = "/rest/v1/dealer/calendar?marketCode=%s&locale=%s&dealerCode=%s";
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
	private String dealerCode = "dealer-code";
	private String marketCode = "GBR";
	private String locale = "en-GB";
	private String modelName = "Fiesta";
	private List<String> additionalService = new ArrayList<String>(Arrays.asList("service-one","service-two"));
	private String motService = "MOT";

	@Test
	public void should_extendTimedHysttrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUWebDealerCalendarCommand.class)).isTrue();
	}

	@Test
	public void shouldReturn_dealerCalendarResponse() {
		
		DealerCalendarV2 expectedData = Utilities.getJsonFileData("dealer-calendar-response.json", DealerCalendarV2.class);
		ResponseEntity<EUOSBDealerCalendarResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealerCalendarResponse.class))).thenReturn(responseEntity);
		DealerCalendarRequest request = createDealerCalendarRequest();
		EUOSBDealerCalendarResponse response = getCommand(request).execute();

		assertNotNull(response);
		assertNotNull(response.getData());
		assertNull(response.getError());
		assertEquals(expectedData, response.getData());
		String osbDealerCalendarUrl = getDealerCalendarUrl(request);
		verify(mockMutualAuthRestTemplate).exchange(contains(osbDealerCalendarUrl), eq(HttpMethod.GET),
				httpEntityCaptor.capture(), eq(EUOSBDealerCalendarResponse.class));
		
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}

	@Test
	public void should_logResponseBodyIfDebugEnabled() throws Exception {

		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUWebDealerCalendarCommand.class);
		ResponseEntity<EUOSBDealerCalendarResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealerCalendarResponse.class))).thenReturn(responseEntity);
		DealerCalendarRequest request = createDealerCalendarRequest();
		EUOSBDealerCalendarResponse actualResponse = getCommand(request).execute();
		final String logs = capturedLogs.toString();

		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUWebDealerCalendarCommand *: Response body*");
	}
	
	@Test
	public void should_constructOsbDealerCalendarUrl() {
		
		DealerCalendarRequest request = createDealerCalendarRequest();
		EUWebDealerCalendarCommand command = getCommand(request);
		String actualUrl = command.constructOsbDealerCalendarUrl(BASE_URL, request);
		String expectedUrl = BASE_URL
				+ String.format(EU_OSB_DEALER_CALENDAR_MANDATORY_PARAMS_PATH, marketCode, locale, dealerCode)
				+ "&modelName=" + modelName + "&additionalServices="
				+ additionalService.get(0) + "," + additionalService.get(1)
				+ "&motServiceID=" + motService;
		assertThat(actualUrl).isEqualTo(expectedUrl);
	}
	
	private DealerCalendarRequest createDealerCalendarRequest() {
		return DealerCalendarRequest.builder()
				.dealerCode(dealerCode)
				.marketCode(marketCode)
				.locale(locale)
				.modelName(modelName)
				.additionalService(additionalService)
				.motService(motService)
				.build();
	}

	private String getDealerCalendarUrl(DealerCalendarRequest request) {

		String url = BASE_URL + String.format(EU_OSB_DEALER_CALENDAR_MANDATORY_PARAMS_PATH, request.getMarketCode(), request.getLocale(), request.getDealerCode());
		
		if (StringUtils.isNotBlank(request.getModelName())) {
			url += "&modelName=" + request.getModelName();
		}
		if (request.getAdditionalService() != null && !request.getAdditionalService().isEmpty()) {
			String commaSeparatedAdditionalServices = null;
			for (String service : request.getAdditionalService()) {
				if (commaSeparatedAdditionalServices == null) {
					commaSeparatedAdditionalServices = "&additionalServices=" + service;
				} else {
					commaSeparatedAdditionalServices += "," + service;
				}
			}
			url += commaSeparatedAdditionalServices;
		}
		if (StringUtils.isNotBlank(request.getMotService())) {
			url += "&motServiceID=" + request.getMotService();
		}
		return url;
	}
	
	private ResponseEntity<EUOSBDealerCalendarResponse> createResponseEntity() {

		DealerCalendarV2 expectedData = Utilities.getJsonFileData("dealer-calendar-response.json", DealerCalendarV2.class);
		EUOSBDealerCalendarResponse expectedResponse = EUOSBDealerCalendarResponse.builder().build();
		expectedResponse.setData(expectedData);
		ResponseEntity<EUOSBDealerCalendarResponse> responseEntity = ResponseEntity.ok(expectedResponse);
		return responseEntity;
	}

	private EUWebDealerCalendarCommand getCommand(DealerCalendarRequest request) {
		
		return new EUWebDealerCalendarCommand(mockTraceInfo, mockMutualAuthRestTemplate, BASE_URL, request);
	}
}
