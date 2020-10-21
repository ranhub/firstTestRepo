package com.ford.turbo.servicebooking.command.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUDealerServicesCommandTest {
	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) 
	private TraceInfo traceInfo;
	
	private String baseUrl = "https://euosb.com";
	private static String path = "/rest/v1/dealer/services";
	private static String queryParams = "?dealerCode=%s&marketCode=%s&locale=%s";
	private String dealerCode = "dealer-code";
	private String marketCode = "GBR"; 
	private String locale = "en-GB";
	private String modelName = "Mustang";
	private String buildYear = "2018"; 
	private String vin = "12345678901234567"; 
	private String registrationNumber = "registration-number"; 
	private String voucherCodes = "10FORD,20FORD";
	private EUDealerServicesCommand command;
	
	private DealerServicesRequest request = new DealerServicesRequest();
	
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor; 
	
	@Before
	public void beforeEachTest() throws IOException {
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}
	
	@Test
	public void should_extendTimedHystrixCommand() throws IOException {
		assertThat(TimedHystrixCommand.class.isAssignableFrom(EUDealerServicesCommand.class)).isTrue();
	}
	
	@Test
	public void shouldContructRequestUrlProperly_whenQueryParamsAreNull() {
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
		
		String url = command.getRequestUrl();
		
		assertThat(url).isEqualTo(baseUrl + path + "?dealerCode=&marketCode=&locale=");
	}
	
	@Test
	public void shouldContructRequestUrlProperly_whenQueryParamsArePresent() {
		DealerServicesRequest request = DealerServicesRequest.builder()
										.dealerCode(dealerCode)
										.marketCode(marketCode)
										.locale(locale)
										.build();
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
		
		String queryParamsExpected = String.format(queryParams, request.getDealerCode(), request.getMarketCode(), request.getLocale());
		String url = command.getRequestUrl();
		
		assertThat(url).isEqualTo(baseUrl + path + queryParamsExpected);
	}
	
	@Test
	public void shouldContructRequestUrlProperly_whenAllQueryParamsArePresent() {
		DealerServicesRequest request = DealerServicesRequest.builder()
										.dealerCode(dealerCode)
										.marketCode(marketCode)
										.locale(locale)
										.vin(vin)
										.registrationNumber(registrationNumber)
										.buildYear(buildYear)
										.modelName(modelName)
										.combinedVoucherCodes(voucherCodes)
										.build();
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
		
		String queryParamsExpected = String.format(queryParams, dealerCode, marketCode, locale) + "&buildYear="
				+ buildYear + "&modelName=" + modelName + "&vin=" + vin + "&registrationNumber=" + registrationNumber
				+ "&voucherCodes=" + voucherCodes;
		
		String url = command.getRequestUrl();
		
		assertThat(url).isEqualTo(baseUrl + path + queryParamsExpected);
	}
	
	@Test
	public void shouldReturnQueryString_whenRequestIsNotEmpty(){
		DealerServicesRequest request = DealerServicesRequest.builder()
				.vin(vin)
				.registrationNumber(registrationNumber)
				.buildYear(buildYear)
				.modelName(modelName)
				.combinedVoucherCodes(voucherCodes)
				.build();
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
		
		String queryParamsExpected = "&buildYear=" + buildYear + "&modelName=" + modelName + "&vin=" + vin
				+ "&registrationNumber=" + registrationNumber + "&voucherCodes=" + voucherCodes;
		
		String queryString = command.constructRequestParams();
		
		assertThat(queryString).isEqualTo(queryParamsExpected);
	}
	
	@Test
	public void shouldReturnQueryString_whenRequestIsEmpty(){
		String vin = null;
		String registrationNumber = null;
		String buildYear = null;
		String modelName = null;
		String voucherCodes = null;
		DealerServicesRequest request = DealerServicesRequest.builder()
				.vin(vin)
				.registrationNumber(registrationNumber)
				.buildYear(buildYear)
				.modelName(modelName)
				.combinedVoucherCodes(voucherCodes)
				.build();
		command = new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
		
		String queryParamsExpected = "";
		
		String queryString = command.constructRequestParams();
		
		assertThat(queryString).isEqualTo(queryParamsExpected);
	}
	
	@Test
	public void shouldReturnEmptyString_when_passedNull(){
		String dealerCode = null;
		String actual = EUDealerServicesCommand.changeEmptyIfNull(dealerCode);
		assertThat(actual).isEqualTo("");
	}
	
	@Test
	public void shouldReturnSamevalue_when_passed(){
		String dealerCode = "dealer-code";
		String actual = EUDealerServicesCommand.changeEmptyIfNull(dealerCode);
		assertThat(actual).isEqualTo(dealerCode);
	}
	
	@Test
	public void shouldReturnDealerServices_when_CalledExecute() throws Exception {
		
		EUOSBDealerServicesResponse servicesResponse = EUOSBDealerServicesResponse.builder().build();
		EUOSBDealerServicesWebResponse euOSBResponse = EUOSBDealerServicesWebResponse.builder().data(servicesResponse).build();
		Mockito.when(mutualAuthRestTemplate.exchange(contains(path), eq(HttpMethod.GET), any(), eq(EUOSBDealerServicesWebResponse.class)))
		.thenReturn(new ResponseEntity<EUOSBDealerServicesWebResponse>(euOSBResponse, HttpStatus.OK));
		
		EUOSBDealerServicesWebResponse response = command.doRun();
		
		Mockito.verify(mutualAuthRestTemplate).exchange(contains(path), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(EUOSBDealerServicesWebResponse.class));
		
		assertThat(response.getError()).isNull();
		assertThat(response.getData()).isEqualTo(servicesResponse);		

	}


}
