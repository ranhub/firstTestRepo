package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.request.EUDealersRequest;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWebWrapper;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.command.eu.web.EUDealerServicesCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUDealersCommand;
import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.Dealer;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealersResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBServiceVoucher;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBWebError;

@RunWith(MockitoJUnitRunner.class)
public class EUDealerServiceTest {
	
	private String gbrMarketCode = "GBR";
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private CredentialsSource mockEuOsbCredentialsSource;
	@Mock
	private EUDealersCommand mockCommand;
	
	private EUDealerService service;

	String dealerCode = "dealer-Code";
	String marketCode = "market-Code";
	String locale = "locale-code";
	String modelName = "model-Name";
	String buildYear = "build-Year";
	String vin = "vin-num";
	String registrationNumber = "registration-Number";
	String voucherCodes = "voucherCodes";
	private static String path = "/rest/v1/dealer/services";
	String euOSBBaseURL = "https://euosb.com";
	private static String queryParams = "?dealerCode=%s&marketCode=%s&locale=%s";
	
	private DealerServicesRequest request = new DealerServicesRequest();
	
	@Before
	public void beforeEachTest() {
		when(mockEuOsbCredentialsSource.getBaseUri()).thenReturn(euOSBBaseURL);
		
		EUDealerService euDealerService = new EUDealerService(mockTraceInfo, mockMutualAuthRestTemplate, mockEuOsbCredentialsSource);
		service = Mockito.spy(euDealerService);
	}
	
	@Test
	public void shouldReturnResponse_whenDealersExistForMarketCode() throws Exception {
		
		ArgumentCaptor<EUDealersRequest>  argumentCaptorEUDealersRequest= ArgumentCaptor.forClass(EUDealersRequest.class);
		EUDealersRequest request = EUDealersRequest.builder().marketCode(gbrMarketCode).build();
		doReturn(mockCommand).when(service).getEUDealersCommand(request);
		EUOSBDealersResponse osbResponse = Utilities.getJsonFileData("euDealersSuccessfulResponse.json", EUOSBDealersResponse.class);
		when(mockCommand.execute()).thenReturn(osbResponse);
		List<Dealer> actualResponse = service.getDealersbyMarketCode(gbrMarketCode);
		
		verify(mockCommand).execute();
		verify(service).getEUDealersCommand(argumentCaptorEUDealersRequest.capture());
		List<Dealer> expectedResponse = osbResponse.getData();
		assertNotNull(actualResponse);
		assertFalse(actualResponse.isEmpty());
		assertEquals(expectedResponse, actualResponse);
		assertThat(argumentCaptorEUDealersRequest.getValue().getMarketCode()).isEqualTo(gbrMarketCode);		
	}
	
	@Test
	public void shouldReturnEUDealersCommand_whenEUDealersRequestPassed() {
		EUDealersRequest request = EUDealersRequest.builder().marketCode(gbrMarketCode).build();
		
		EUDealersCommand command = service.getEUDealersCommand(request);
		
		assertNotNull(command);
		assertTrue(command instanceof TimedHystrixCommand);
	}
	
	@Test
	public void shouldReturnNull_when_ServiceVoucherListIsEmpty(){
		List<ServiceVoucher> serviceVouchersList = service.convertToServiceVouchers(null);
		
		assertThat(serviceVouchersList).isNull();
	}
	
	@Test
	public void shouldReturnServiceVoucherList_when_euServiceVouchersAreAvailable() {
		BigDecimal amount = new BigDecimal("12.5");
		String description = "voucher-code-description";
		Long percentage = 100l;
		String code = "10FORD"; 
		
		List<EUOSBServiceVoucher> euServiceVouchers = new ArrayList<>();
		EUOSBServiceVoucher voucher1 = EUOSBServiceVoucher.builder()
										.voucherAmount(amount)
										.voucherCodeDescription(description)
										.voucherPercentage(percentage)
										.voucherCode(code)
										.build();
		euServiceVouchers.add(voucher1);
		
		List<ServiceVoucher> expectedServiceVoucherList = new ArrayList<>();
		ServiceVoucher serviceVoucher = ServiceVoucher.builder()
				.amount(amount)
				.description(description)
				.percentage(percentage)
				.code(code)
				.build();
		expectedServiceVoucherList.add(serviceVoucher);		
		
		List<ServiceVoucher> serviceVouchersList = service.convertToServiceVouchers(euServiceVouchers);
		
		assertThat(serviceVouchersList).isEqualTo(expectedServiceVoucherList);
	}
	
	@Test
	public void shouldReturnNull_when_euOldServicesIsEmpty(){
		List<OldServicesWeb> list = service.convertToOldServicesWeb(null);
		assertThat(list).isNull();
	}
	
	@Test
	public void shouldReturnOldServiceWebList_when_euOSBOldServicesAreAvailable(){
		String description = "description";
		Long discountPercentage = 100l;
		BigDecimal discountPrice =new BigDecimal("12.5");
		BigDecimal price = new BigDecimal("67.7");
		BigDecimal priceAfterDiscount = new BigDecimal("67.8");
		
		String name = "old-service-name";
		String serviceId = "service-id";
		String subType = "subType";
		EUOSBOldServiceResponse euOSBOldService =  EUOSBOldServiceResponse.builder()
													.description(description)
													.discountPercentage(discountPercentage)
													.discountPrice(discountPrice)
													.price(price)
													.priceAfterDiscount(priceAfterDiscount)
													.name(name)
													.serviceId(serviceId)
													.subType(subType)
													.build();
		List<EUOSBOldServiceResponse> euOsbOldServicesList = new ArrayList<>();
		euOsbOldServicesList.add(euOSBOldService);
		
		OldServicesWeb outputObject = OldServicesWeb.builder()
										.description(description)
										.discountPercentage(discountPercentage)
										.discountPrice(discountPrice)
										.name(name)
										.price(price)
										.priceAfterDiscount(priceAfterDiscount)
										.serviceId(serviceId)
										.subType(subType)
										.build();
		List<OldServicesWeb> expectedOldServicesList = new ArrayList<>();
		expectedOldServicesList.add(outputObject);
		
		List<OldServicesWeb> actualOldServicesList = service.convertToOldServicesWeb(euOsbOldServicesList);
	
		assertThat(actualOldServicesList).isEqualTo(expectedOldServicesList);
	}
	
	@Test
	public void shouldReturnNull_when_euOSBAdditionalServicesIsNull(){
		
		List<AdditionalServicesWeb> actualexpectedAdditionalServicesWebListList = service.convertToAdditionalServicesWeb(null);
		assertThat(actualexpectedAdditionalServicesWebListList).isNull();
	}
	
	@Test
	public void shouldReturnAdditionalServicesWebList_when_euOSBAdditionalServicesListIsAvailable(){
		List<EUOSBAdditionalServiceResponse> euOSBAdditionalServices = new ArrayList<>();
		String description = "description";
		String name = "name";
		BigDecimal price = new BigDecimal("67.7");
		Long discountPercentage = 100l;
		BigDecimal discountPrice = new BigDecimal("12.5");
		BigDecimal priceAfterDiscount = new BigDecimal("67.8");
		boolean selected = false;
		String serviceId = "serviceId";
		EUOSBAdditionalServiceResponse additionalService = EUOSBAdditionalServiceResponse.builder()
															.description(description)
															.name(name)
															.discountPercentage(discountPercentage)
															.discountPrice(discountPrice)
															.price(price)
															.priceAfterDiscount(priceAfterDiscount)
															.selected(selected)
															.serviceId(serviceId)
															.build();
		euOSBAdditionalServices.add(additionalService);
		
		List<AdditionalServicesWeb> expectedAdditionalServicesWebList = new ArrayList<>();
		AdditionalServicesWeb additionalServiceWeb = AdditionalServicesWeb.builder()
														.description(description)
														.name(name)
														.discountPercentage(discountPercentage)
														.discountPrice(discountPrice)
														.price(price)
														.priceAfterDiscount(priceAfterDiscount)
														.selected(selected)
														.serviceId(serviceId)
														.build();
		expectedAdditionalServicesWebList.add(additionalServiceWeb);
		
		List<AdditionalServicesWeb> actualexpectedAdditionalServicesWebListList = service.convertToAdditionalServicesWeb(euOSBAdditionalServices);
	
		assertThat(actualexpectedAdditionalServicesWebListList).isEqualTo(expectedAdditionalServicesWebList);
	}
	
	@Test
	public void shouldReturnNull_when_euOSBMainServicesIsEmpty(){
		List<MainServicesWeb> expectedList = service.convertToMainServicesWeb(null);
		assertThat(expectedList).isNull();
	}

	@Test 
	public void shouldReturnMainServicesWebList_when_mainServicesAreAvailable(){
		List<EUOSBMainServiceResponse> euOSBMainServices = new ArrayList<>();
		String applicationInformation = "addiontal-information";
		String description = "description";
		String name = "name";
		BigDecimal price = new BigDecimal("67.7");
		Long discountPercentage = 100l;
		BigDecimal discountPrice = new BigDecimal("12.5");
		BigDecimal priceAfterDiscount = new BigDecimal("67.8");
		String serviceId = "serviceId";
		String subType = "subType";
		EUOSBMainServiceResponse mainService = EUOSBMainServiceResponse.builder()
												.applicationInformation(applicationInformation)
												.description(description)
												.name(name)
												.discountPercentage(discountPercentage)
												.discountPrice(discountPrice)
												.price(price)
												.priceAfterDiscount(priceAfterDiscount)
												.serviceId(serviceId)
												.subType(subType)
												.build();
		euOSBMainServices.add(mainService);
		
		List<MainServicesWeb> expectedMainServicesWebList = new ArrayList<>();
		MainServicesWeb mainServicesWeb = MainServicesWeb.builder()
											.applicationInformation(applicationInformation)
											.description(description)
											.name(name)
											.discountPercentage(discountPercentage)
											.discountPrice(discountPrice)
											.price(price)
											.priceAfterDiscount(priceAfterDiscount)
											.serviceId(serviceId)
											.subType(subType)
											.build();
		expectedMainServicesWebList.add(mainServicesWeb);
		
		List<MainServicesWeb> actualMainServicesWebList = service.convertToMainServicesWeb(euOSBMainServices);
		assertThat(actualMainServicesWebList).isEqualTo(expectedMainServicesWebList);
	}
	
	@Test
	public void shouldReturnNull_when_euOSBResponseIsnull(){
		DealerServicesWebWrapper wrapper = service.convertToMSLResponse(null);
		assertThat(wrapper).isNull();
	}
	
	@Test
	public void shouldReturnMSLResponse_when_passedeuOSBResponse(){
		EUOSBDealerServicesResponse euOSBResponse = EUOSBDealerServicesResponse.builder().build();
		DealerServicesWebWrapper wrapper = service.convertToMSLResponse(euOSBResponse);
		
		assertThat(wrapper.getDealerServices()).isNotNull();
		Assertions.assertThat(wrapper.getDealerServices().getMainServices()).isNull();
		Assertions.assertThat(wrapper.getDealerServices().getAdditionalServices()).isNull();
		Assertions.assertThat(wrapper.getDealerServices().getOldServices()).isNull();
		Assertions.assertThat(wrapper.getDealerServices().getServiceVouchers()).isNull();
		
		verify(service).convertToMainServicesWeb(euOSBResponse.getMainServices());
		verify(service).convertToAdditionalServicesWeb(euOSBResponse.getAdditionalServices());
		verify(service).convertToOldServicesWeb(euOSBResponse.getOldServices());
		verify(service).convertToServiceVouchers(euOSBResponse.getVoucherCodes());
	}
	
	@Test
	public void shouldGiveNull_whenVoucherCodeListIsNull() {
		String combinedVoucherCodes = service.getCombinedVoucherCodes(null);
		
		assertThat(combinedVoucherCodes).isNull();
	}
	
	@Test
	public void shouldGiveNull_whenVoucherCodeListIsEmptyList() {
		String combinedVoucherCodes = service.getCombinedVoucherCodes(new ArrayList<>());
		
		assertThat(combinedVoucherCodes).isNull();
	}
	
	@Test
	public void shouldReturnSameString_when_SingleVoucherCodeIsGiven() {
		String voucherCode = "voucher-code-1";
		String combinedVoucherCodes = service.getCombinedVoucherCodes(Arrays.asList(voucherCode));
		
		assertThat(combinedVoucherCodes).isEqualTo(voucherCode);
	}
	
	@Test
	public void shouldGiveString_whenVoucherCode_whenOneOfTheVoucherCodeIsEmpty() {
		String voucherCode1 = "voucher-code-1";
		String voucherCode2 = "";
		String voucherCode3 = "voucher-code-3";
		String voucherCode4 = null;
		
		String combinedVoucherCodes = service.getCombinedVoucherCodes(Arrays.asList(voucherCode1, voucherCode2, voucherCode3, voucherCode4));
		
		assertThat(combinedVoucherCodes).isEqualTo(voucherCode1 + "," + voucherCode3);
	}

	@Test
	public void shouldReturnEUDealerServicesCommand_whenServiceParamsPassed() {
		EUDealerServicesCommand command = service.getDealerServicesCommand(request);
		
		assertNotNull(command);
		assertTrue(command instanceof TimedHystrixCommand);
	}
	
	@Test
	public void shouldReturnDealerServices_andVerifyCommandExecution_when_InputGiven() {
		EUOSBDealerServicesResponse dealerServicesResponse = EUOSBDealerServicesResponse.builder().build();
		EUOSBDealerServicesWebResponse euServicesResponse = EUOSBDealerServicesWebResponse.builder().data(dealerServicesResponse).build();
		EUDealerServicesCommand command = mock(EUDealerServicesCommand.class);
		doReturn(command).when(service)
			.getDealerServicesCommand(request);
		when(command.execute()).thenReturn(euServicesResponse);
		
		service.getDealerServices(request);
		
		verify(service).getCombinedVoucherCodes(request.getVoucherCode());
		verify(command).execute();
		verify(service).getDealerServicesCommand(request);
		verify(service).convertToMSLResponse(euServicesResponse.getData());
	}
	
	@Test(expected = BadRequestException.class)
	public void shouldReturnOSBError_whenInvalidMarketIsPassed() {
		EUOSBWebError error = EUOSBWebError.builder().message("Invalid marketCode supplied for the locale: en-GB").code("OSB_INVALID_MARKETCODE").statusCode("400").build();
		EUOSBDealerServicesWebResponse euServicesResponse = EUOSBDealerServicesWebResponse.builder().error(error).build();
		EUDealerServicesCommand command = mock(EUDealerServicesCommand.class);
		doReturn(command).when(service)
			.getDealerServicesCommand(request);
		when(command.execute()).thenReturn(euServicesResponse);
		
		service.getDealerServices(request);
		
		verify(service).getCombinedVoucherCodes(request.getVoucherCode());
		verify(command).execute();
		verify(service).getDealerServicesCommand(request);
		verify(service).convertToMSLResponse(euServicesResponse.getData());
	}
	
	@Test
	public void shouldReturnDealerServices_andVerifyCommandParams() {
		getDealerParamsRequest();
		String expectedURL = euOSBBaseURL + path +
								String.format(queryParams, request.getDealerCode(), request.getMarketCode(), request.getLocale()) +
								"&buildYear=" + request.getBuildYear() +
								"&modelName=" + request.getModelName() + 
								"&vin=" + request.getVin() + 
								"&registrationNumber=" + request.getRegistrationNumber() +
								"&voucherCodes=" + "voucher-code1,voucher-code2";
		
		EUOSBDealerServicesResponse dealerServicesResponse = EUOSBDealerServicesResponse.builder().build();
		EUOSBDealerServicesWebResponse euServicesResponse = EUOSBDealerServicesWebResponse.builder().data(dealerServicesResponse).build();

		when(mockMutualAuthRestTemplate.exchange(contains(path), eq(HttpMethod.GET), any(), eq(EUOSBDealerServicesWebResponse.class)))
		.thenReturn(new ResponseEntity<EUOSBDealerServicesWebResponse>(euServicesResponse, HttpStatus.OK));

		service.getDealerServices(request);
		
		verify(mockMutualAuthRestTemplate).exchange(eq(expectedURL), eq(HttpMethod.GET), any(), eq(EUOSBDealerServicesWebResponse.class));
		verify(service).getCombinedVoucherCodes(request.getVoucherCode());
		verify(service).getDealerServicesCommand(request);
		verify(service).convertToMSLResponse(euServicesResponse.getData());
	}
	
	private void getDealerParamsRequest(){
		request = DealerServicesRequest.builder()
				.dealerCode(dealerCode)
				.marketCode(marketCode)
				.locale(locale)
				.vin(vin)
				.registrationNumber(registrationNumber)
				.buildYear(buildYear)
				.modelName(modelName)
				.voucherCode(Arrays.asList("voucher-code1,voucher-code2"))
				.build();
	}
}

	
