package com.ford.turbo.servicebooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.response.AdditionalService;
import com.ford.turbo.servicebooking.models.msl.response.MainService;
import com.ford.turbo.servicebooking.models.msl.response.OSBOVService;
import com.ford.turbo.servicebooking.models.msl.response.ServicesListResponse;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.PropertySource;

import com.ford.turbo.servicebooking.command.eu.web.EUDealerServicesCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBWebError;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class EUListDetailInformationServiceTest {

	
	@Mock
	private CredentialsSource credentialsSource;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TraceInfo traceInfo;
	
	
	@Mock
	private EUDealerServicesCommand mockCommand;
	
	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	
	
	@Spy
	@InjectMocks
	private EUListDetailInformationService detailInformationService;
	

	@Test
	public void shouldReturn_servicesListResponse() {
			
		ServicesListResponse expectedResponse = getMSLServiceListResponse();
		
		DealerServicesRequest request = getDealerServicesRequest();
		doReturn(mockCommand).when(detailInformationService).getEUDealerServicesCommand(request);
		
		EUOSBDealerServicesWebResponse osbResponse = getOSBServicesResponse();

		when(mockCommand.execute()).thenReturn(osbResponse);
		ServicesListResponse actualResponse = detailInformationService.listServices(LanguageCode.EN, RegionCode.GB,
				CountryCode.GBR, "10000", "12345678901234567", "dealer-code",
				Arrays.asList("voucher-code-1", "voucher-code-2"));
		assertNotNull(actualResponse);
		verify(detailInformationService)
			.getDealerServicesRequest(LanguageCode.EN, RegionCode.GB, CountryCode.GBR, "10000", "12345678901234567", "dealer-code", Arrays.asList("voucher-code-1", "voucher-code-2"));
		verify(mockCommand).execute();
		verify(detailInformationService).getEUDealerServicesCommand(request);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void shouldReturn_EUDealerServicesCommand() {
		
		DealerServicesRequest request = getDealerServicesRequest();
		EUDealerServicesCommand command = detailInformationService.getEUDealerServicesCommand(request);
		assertNotNull(command);
		assertTrue(command instanceof TimedHystrixCommand);
	}

	private DealerServicesRequest getDealerServicesRequest() {
		return DealerServicesRequest.builder()
				.dealerCode("dealer-code")
				.marketCode("GBR")
				.locale("en-gb")
				.modelName(null)
				.buildYear(null)
				.vin("12345678901234567")
				.registrationNumber(null)
				.voucherCode(Arrays.asList("voucher-code-1", "voucher-code-2"))
				.combinedVoucherCodes("voucher-code-1,voucher-code-2")
				.mileage("10000")
				.build();
	}
	
	@Test
	public void shouldReturn_dealerServicesRequest() {
		
		DealerServicesRequest expected = getDealerServicesRequest();
		
		DealerServicesRequest actual = detailInformationService.getDealerServicesRequest(LanguageCode.EN, RegionCode.GB, CountryCode.GBR,
                "10000", "12345678901234567", "dealer-code", Arrays.asList("voucher-code-1", "voucher-code-2"));
		
		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	public void shouldReturn_dealerServicesRequest_withVoucherCodesNull() {
		
		DealerServicesRequest expected = DealerServicesRequest.builder()
				.dealerCode("dealer-code")
				.marketCode("GBR")
				.locale("en-gb")
				.modelName(null)
				.buildYear(null)
				.vin("12345678901234567")
				.registrationNumber(null)
				.voucherCode(null)
				.combinedVoucherCodes(null)
				.mileage("10000")
				.build();
		
		DealerServicesRequest actual = detailInformationService.getDealerServicesRequest(LanguageCode.EN, RegionCode.GB, CountryCode.GBR,
                "10000", "12345678901234567", "dealer-code", null);
		assertThat(actual).isEqualTo(expected);
	}
		
	@Test
	public void testMapOsbResponseToMSLResponse() throws Exception {
		ServicesListResponse expectedResponse = getMSLServiceListResponse();
		EUOSBDealerServicesWebResponse osbResponse = getOSBServicesResponse();
		ServicesListResponse actualResponse = detailInformationService.mapOSBServicesToMSLServicesResponse(osbResponse);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testMapOsbResponseToMSLResponse_when_additional_services_null() throws Exception {
		ServicesListResponse expectedResponse = getMSLServiceListResponse();
		expectedResponse.setAdditional(new ArrayList<>());
		EUOSBDealerServicesWebResponse osbResponse = getOSBServicesResponse();
		osbResponse.getData().setAdditionalServices(null);
		ServicesListResponse actualResponse = detailInformationService.mapOSBServicesToMSLServicesResponse(osbResponse);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testMapOsbResponseToMSLResponse_when_main_services_null() throws Exception {
		ServicesListResponse expectedResponse = getMSLServiceListResponse();
		expectedResponse.setMain(new ArrayList<>());
		EUOSBDealerServicesWebResponse osbResponse = getOSBServicesResponse();
		osbResponse.getData().setMainServices(null);
		ServicesListResponse actualResponse = detailInformationService.mapOSBServicesToMSLServicesResponse(osbResponse);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	public void testMapOsbResponseToMSLResponse_when_old_services_null() throws Exception {
		ServicesListResponse expectedResponse = getMSLServiceListResponse();
		expectedResponse.setOldServices(new ArrayList<>());
		EUOSBDealerServicesWebResponse osbResponse = getOSBServicesResponse();
		osbResponse.getData().setOldServices(null);
		ServicesListResponse actualResponse = detailInformationService.mapOSBServicesToMSLServicesResponse(osbResponse);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test (expected = BadRequestException.class)
	public void shouldThrow_whenOSBReturnError() {
		
		DealerServicesRequest request = getDealerServicesRequest();
		doReturn(mockCommand).when(detailInformationService).getEUDealerServicesCommand(request);
		
		EUOSBDealerServicesWebResponse osbResponse = EUOSBDealerServicesWebResponse.builder().build();
		EUOSBWebError error =new EUOSBWebError();
		error.setCode("OSB_NO_DATAFOUND");
		error.setMessage("OSB_NO_DATAFOUND");
		error.setStatusCode("204");
		
		osbResponse.setError(error);
		when(mockCommand.execute()).thenReturn(osbResponse);
		ServicesListResponse actualResponse = detailInformationService.listServices(LanguageCode.EN, RegionCode.GB,
				CountryCode.GBR, "10000", "12345678901234567", "dealer-code",
				Arrays.asList("voucher-code-1", "voucher-code-2"));
		assertNotNull(actualResponse);
		verify(detailInformationService)
			.getDealerServicesRequest(LanguageCode.EN, RegionCode.GB, CountryCode.GBR, "10000", "12345678901234567", "dealer-code", Arrays.asList("voucher-code-1", "voucher-code-2"));
		verify(mockCommand).execute();
		verify(detailInformationService).getEUDealerServicesCommand(request);
		
		
	
	}
	

	private ServicesListResponse getMSLServiceListResponse() {
		ServicesListResponse expectedResponse = new ServicesListResponse();
		expectedResponse.setMain(getMSLMainServices());
		expectedResponse.setAdditional(getMSLAdditionalServices());
		expectedResponse.setOldServices(getMSLOldServices());
		return expectedResponse;
	}
	private List<MainService> getMSLMainServices(){
		
		List<MainService> mainServices=new ArrayList<MainService>();
		MainService mainService = new MainService();
		mainService.setName("MainService");
		mainService.setSubType("Major");
		mainService.setPrice(new BigDecimal(35.35));
		mainService.setPriceAfterDiscount(new BigDecimal(30.00));
		mainService.setDiscountPrice(new BigDecimal(5.35));
		mainService.setDiscountPercentage(10l);
		mainService.setServiceId("66666");
		mainService.setDescription("Mainservice description");
		mainService.setApplicationInformation("Main Service applicationInformation");
		mainServices.add(mainService);
		return mainServices;
	}
	
	
	private List<AdditionalService> getMSLAdditionalServices(){
		List<AdditionalService>  additionalServices = new ArrayList<AdditionalService>();
		
		AdditionalService additionalService = new AdditionalService();
		additionalService.setName("Additional Service 1");
		additionalService.setPrice(new BigDecimal(45.35));
		additionalService.setPriceAfterDiscount(new BigDecimal(35.35));
		additionalService.setDiscountPrice(new BigDecimal(10.00));
		additionalService.setDiscountPercentage(10l);
		additionalService.setServiceId("1");
		additionalService.setDescription("Additional Service description1");
		additionalServices.add(additionalService);
		
		additionalService = new AdditionalService();
		additionalService.setName("Additional Service 2");
		additionalService.setPrice(new BigDecimal(25.35));
		additionalService.setPriceAfterDiscount(new BigDecimal(20.00));
		additionalService.setDiscountPrice(new BigDecimal(5.35));
		additionalService.setDiscountPercentage(10l);
		additionalService.setServiceId("2");
		additionalService.setDescription("Additional Service description2");
		additionalServices.add(additionalService);
		return additionalServices;
		
	}
	
	private List<OSBOVService> getMSLOldServices(){
		List<OSBOVService> mslOldServices = new ArrayList<OSBOVService>();
		
		OSBOVService osbovService = new OSBOVService();
		
		osbovService.setName("MOT");
		osbovService.setDescription("Old Mot Service");
		osbovService.setServiceId("3005:MOT");
		osbovService.setPrice("35.00");
		osbovService.setPriceAfterDiscount("30.00");
		osbovService.setDiscountPrice("5.00");
		osbovService.setDiscountPercentage("5");
		osbovService.setSubType(OldServiceType.MOT);
		mslOldServices.add(osbovService);
		
		osbovService = new OSBOVService();
		osbovService.setName("REPAIR");
		osbovService.setDescription("Replace timing belt");
		osbovService.setServiceId("3077:REPAIR");
		osbovService.setPrice("45.00");
		osbovService.setPriceAfterDiscount("35.00");
		osbovService.setDiscountPrice("10.00");
		osbovService.setDiscountPercentage("5");
		osbovService.setSubType(OldServiceType.REPAIR);
		mslOldServices.add(osbovService);
		
		osbovService = new OSBOVService();
		osbovService.setName("Motorcraft Service");
		osbovService.setDescription("Motorcraft Service");
		osbovService.setServiceId("3019:VALUE");
		osbovService.setPrice("55.00");
		osbovService.setPriceAfterDiscount("40.00");
		osbovService.setDiscountPrice("15.00");
		osbovService.setDiscountPercentage("10");
		osbovService.setSubType(OldServiceType.VALUE);
		mslOldServices.add(osbovService);
		return mslOldServices;
		
	}
	
	private EUOSBDealerServicesWebResponse getOSBServicesResponse() {
		EUOSBDealerServicesWebResponse osbResponse = EUOSBDealerServicesWebResponse.builder().build();
		EUOSBDealerServicesResponse data = new EUOSBDealerServicesResponse();
		data.setMainServices(getOSBMainServices());
		data.setAdditionalServices(getOSBAdditionalServices());
		data.setOldServices(getOSBOldServices());
		osbResponse.setData(data);
		return osbResponse;
	}


	private List<EUOSBOldServiceResponse> getOSBOldServices() {
		List<EUOSBOldServiceResponse> oldServices = new ArrayList<EUOSBOldServiceResponse>();
		
		EUOSBOldServiceResponse oldServiceResponse = new EUOSBOldServiceResponse();
		oldServiceResponse.setName("MOT");
		oldServiceResponse.setDescription("Old Mot Service");
		oldServiceResponse.setServiceId("3005");
		oldServiceResponse.setPrice(new BigDecimal(35.00));
		oldServiceResponse.setPriceAfterDiscount(new BigDecimal(30.00));
		oldServiceResponse.setDiscountPrice(new BigDecimal(5.00));
		oldServiceResponse.setDiscountPercentage(5l);
		oldServiceResponse.setSubType("MOT");
		oldServices.add(oldServiceResponse);
		
		 oldServiceResponse = new EUOSBOldServiceResponse();
		 oldServiceResponse.setName("REPAIR");
		 oldServiceResponse.setDescription("Replace timing belt");
		 oldServiceResponse.setServiceId("3077");
		 oldServiceResponse.setPrice(new BigDecimal(45.00));
		 oldServiceResponse.setPriceAfterDiscount(new BigDecimal(35.00));
		 oldServiceResponse.setDiscountPrice(new BigDecimal(10.00));
		 oldServiceResponse.setDiscountPercentage(5l);
		 oldServiceResponse.setSubType("REPAIR");
		oldServices.add(oldServiceResponse);
		
		
		 oldServiceResponse = new EUOSBOldServiceResponse();
		 oldServiceResponse.setName("Motorcraft Service");
		 oldServiceResponse.setDescription("Motorcraft Service");
		 oldServiceResponse.setServiceId("3019");
		 oldServiceResponse.setPrice(new BigDecimal(55.00));
		 oldServiceResponse.setPriceAfterDiscount(new BigDecimal(40.00));
		 oldServiceResponse.setDiscountPrice(new BigDecimal(15.00));
		 oldServiceResponse.setDiscountPercentage(10l);
		 oldServiceResponse.setSubType("VALUE");
		 oldServices.add(oldServiceResponse);
		return oldServices;
	}


	private List<EUOSBAdditionalServiceResponse> getOSBAdditionalServices() {
		List<EUOSBAdditionalServiceResponse> additionalServices = new ArrayList<EUOSBAdditionalServiceResponse>();
		
		EUOSBAdditionalServiceResponse euOSbAdditionalService = new EUOSBAdditionalServiceResponse();
		euOSbAdditionalService.setName("Additional Service 1");
		euOSbAdditionalService.setPrice(new BigDecimal(45.35));
		euOSbAdditionalService.setPriceAfterDiscount(new BigDecimal(35.35));
		euOSbAdditionalService.setDiscountPrice(new BigDecimal(10.00));
		euOSbAdditionalService.setDiscountPercentage(10l);
		euOSbAdditionalService.setServiceId("1");
		euOSbAdditionalService.setDescription("Additional Service description1");
		additionalServices.add(euOSbAdditionalService);
		
		euOSbAdditionalService = new EUOSBAdditionalServiceResponse();
		euOSbAdditionalService.setName("Additional Service 2");
		euOSbAdditionalService.setPrice(new BigDecimal(25.35));
		euOSbAdditionalService.setPriceAfterDiscount(new BigDecimal(20.00));
		euOSbAdditionalService.setDiscountPrice(new BigDecimal(5.35));
		euOSbAdditionalService.setDiscountPercentage(10l);
		euOSbAdditionalService.setServiceId("2");
		euOSbAdditionalService.setDescription("Additional Service description2");
		additionalServices.add(euOSbAdditionalService);
		return additionalServices;
	}


	private List<EUOSBMainServiceResponse> getOSBMainServices() {
		List<EUOSBMainServiceResponse> osbMainServices = new ArrayList<EUOSBMainServiceResponse>();
		EUOSBMainServiceResponse euosbMainService = new EUOSBMainServiceResponse();
		
		euosbMainService.setName("MainService");
		euosbMainService.setSubType("Major");
		euosbMainService.setPrice(new BigDecimal(35.35));
		euosbMainService.setPriceAfterDiscount(new BigDecimal(30.00));
		euosbMainService.setDiscountPrice(new BigDecimal(5.35));
		euosbMainService.setDiscountPercentage(10l);
		euosbMainService.setServiceId("66666");
		euosbMainService.setDescription("Mainservice description");
		euosbMainService.setApplicationInformation("Main Service applicationInformation");
		osbMainServices.add(euosbMainService);
		return osbMainServices;
	}
}