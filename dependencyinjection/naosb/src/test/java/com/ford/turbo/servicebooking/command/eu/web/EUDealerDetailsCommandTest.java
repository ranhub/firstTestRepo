
package com.ford.turbo.servicebooking.command.eu.web;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerDetailsResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUDealerDetailsCommandTest {

	private final String BASE_URL = "http://dealers.com";
	private static final String EU_OSB_DEALER_INFO_PATH = "/rest/v1/dealer/dealerInfo";

	String vin = "12345678901234567";
	String registrationNumber = "REG 1234";
	String locale = "en-gb";
	String marketCode = "GBR";
	String modelName = "Fiesta";
	String buildYear = "2013";

	private List<String> dealerCodeList = Arrays.asList("12345", "123456");
	private EUDealerDetailsCommand command;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	private ObjectMapper mapper = new ObjectMapper();
	private String dealerInfoUrl = "/rest/v1/dealer/dealerInfo";
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

	@Test
	public void should_extendTimedHystrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUDealerDetailsCommand.class)).isTrue();
	}

	@Test
	public void shouldReturn_listOfEUDealerDetails() throws Exception {
		EUOSBDealerDetailsResponse expectedResponse = Utilities
				.getJsonFileData("euDealerDetailsSuccessfulResponse.json", EUOSBDealerDetailsResponse.class);
		ResponseEntity<EUOSBDealerDetailsResponse> responseEntity = ResponseEntity.ok(expectedResponse);
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealerDetailsResponse.class))).thenReturn(responseEntity);
		
		EUOSBDealerDetailsResponse actualResponse = getCommand().execute();

		assertThat(actualResponse).isNotNull();
		assertThat(actualResponse.getData()).isNotNull();
		assertThat(actualResponse.getError()).isNull();
		assertEquals(expectedResponse, actualResponse);
		verify(mockMutualAuthRestTemplate).exchange(contains(dealerInfoUrl), eq(HttpMethod.GET), httpEntityCaptor.capture(),
				eq(EUOSBDealerDetailsResponse.class));
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}

	@Test
	public void should_logResponseHeaders() throws Exception {
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,
				EUDealerDetailsCommand.class);
		ResponseEntity<EUOSBDealerDetailsResponse> responseEntity = createResponseEntity();
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealerDetailsResponse.class))).thenReturn(responseEntity);
		EUOSBDealerDetailsResponse actualResponse = getCommand().execute();
		final String logs = capturedLogs.toString();

		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUDealerDetailsCommand *: Response body*");
	}

	@Test
	public void shouldReturnOsbURL_withVin() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setVin(vin);
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}
	
   @Test
	public void shouldReturnOsbURL_withDealerCodesEmpty() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setVin(vin);
		request.setDealerCodes(new ArrayList<String>());
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}
   
   @Test
	public void shouldReturnOsbURL_withOneDealerCode() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setVin(vin);
		List<String> dealerCodes=new ArrayList<String>();
		dealerCodes.add("12345");
		request.setDealerCodes(dealerCodes);
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}
   
	@Test
	public void shouldReturnOsbURL_withRegistrationNumber() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setRegistrationNumber(registrationNumber);
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void shouldReturnOsbURL_withModelNameAndBuildYear() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setModelName(modelName);
		request.setBuildYear(buildYear);
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void shouldReturnOsbURL_withAllParameters() {
		DealersDetailsRequest request = createEUDealersRequest();
		request.setVin(vin);
		request.setRegistrationNumber(registrationNumber);
		request.setModelName(modelName);
		request.setBuildYear(buildYear);
		String expectedUrl = constructExptectedUrl(request);
		String actualUrl = getCommand().constructRequestUrl(BASE_URL, request);
		assertEquals(expectedUrl, actualUrl);
	}

	private String constructExptectedUrl(DealersDetailsRequest request) {
		String requestUrl = BASE_URL + EU_OSB_DEALER_INFO_PATH + "?marketCode=" + request.getMarketCode() + "&locale="
				+ request.getLocale() ;
		String commaSeparatedDealerCodes = null;
		for (String dealerCode : request.getDealerCodes()) {
			if (commaSeparatedDealerCodes == null) {
				commaSeparatedDealerCodes = dealerCode;
			} else {
				commaSeparatedDealerCodes += "," + dealerCode;
			}
		}
		if(StringUtils.isNotBlank(commaSeparatedDealerCodes)) {
			requestUrl += "&dealerCodes=" + commaSeparatedDealerCodes;
		}
		if (StringUtils.isNotBlank(request.getVin())) {
			requestUrl += "&vin=" + request.getVin();
		}
		if (StringUtils.isNotBlank(request.getRegistrationNumber())) {
			requestUrl += "&registrationNumber=" + request.getRegistrationNumber();
		}
		if (StringUtils.isNotBlank(request.getModelName())) {
			requestUrl += "&modelName=" + request.getModelName();
		}
		if (StringUtils.isNotBlank(request.getBuildYear())) {
			requestUrl += "&buildYear=" + request.getBuildYear();
		}
		return requestUrl;
	}

	private ResponseEntity<EUOSBDealerDetailsResponse> createResponseEntity()
			throws UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {

		Resource resource = new ClassPathResource("euDealerDetailsSuccessfulResponse.json");
		String responseString = new String(readAllBytes(resource.getFile().toPath()), "UTF8");
		ResponseEntity<EUOSBDealerDetailsResponse> responseEntity = ResponseEntity
				.ok(mapper.readValue(responseString, EUOSBDealerDetailsResponse.class));
		return responseEntity;
	}

	private DealersDetailsRequest createEUDealersRequest() {
		DealersDetailsRequest request = new DealersDetailsRequest();
		request.setDealerCodes(dealerCodeList);
		request.setMarketCode(marketCode);
		request.setLocale(locale);

		return request;
	}

	private EUDealerDetailsCommand getCommand() {
		command = new EUDealerDetailsCommand(traceInfo, mockMutualAuthRestTemplate, BASE_URL, createEUDealersRequest());
		return command;
	}
}
