package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.response.AccessCodesNotificationWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.CancelBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.GetBookingsWebWrapper;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.web.EUAccessCodesNotificationCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUCancelBookingCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUGetBookingsCommand;
import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.CustomerWeb;
import com.ford.turbo.servicebooking.models.eu.web.DealerProfile;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWeb;
import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
import com.ford.turbo.servicebooking.models.msl.request.CancelBookingRequest;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.request.EUWebCustomer;
import com.ford.turbo.servicebooking.models.msl.request.GetBookingsRequest;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponseData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBBookedServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponseData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCustomerResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerProfileResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBGetBookingsData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBServiceVoucher;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleDetailsResponse;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;
import com.ford.turbo.servicebooking.models.osb.request.EUOSBCustomer;
import com.ford.turbo.servicebooking.models.osb.request.OSBCreateBookingAdditionalService;

@RunWith(MockitoJUnitRunner.class)
public class EUWebBookingServiceTest {

	@Spy
	@InjectMocks
	private EUWebBookingService service;
	
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private CredentialsSource mockEuOsbCredentialsSource;
	@Mock
	private EUAccessCodesNotificationCommand mockEUAccessCodesNotificationCommand;
	
	@Mock
	private EUCancelBookingCommand mockEUCancelBookingCommand;
	
	private String bookingReferenceNumber = "booking-reference-123";
	private String accessCode = "access-code-123";
	private String email = "email@email.com";
	private boolean osbSiteTermsRequired = true;
	
	@Test
	public void shouldReturnResponse_whenAccessCodeNotificationSent() throws Exception {

		EUOSBAccessCodesNotificationResponse osbResponse = EUOSBAccessCodesNotificationResponse.builder().build();
		osbResponse.setData(EUOSBAccessCodesNotificationResponseData.builder().isReminderSent(false).build());
		AccessCodesNotificationRequest request = createAccessCodeNotificationRequest("GBR", "henry@ford.com", true);
		doReturn(mockEUAccessCodesNotificationCommand).when(service).getEUAccessCodeNotificationCommand(request);
		when(mockEUAccessCodesNotificationCommand.execute()).thenReturn(osbResponse);
		AccessCodesNotificationWebWrapper wrapper = service.sendAccessCodesNotification(request);
		
		assertNotNull(wrapper);
		assertFalse(wrapper.isNotified());
		verify(mockEUAccessCodesNotificationCommand).execute();
		verify(service).getEUAccessCodeNotificationCommand(request);
	}

	@Test
	public void shouldReturnEUAccessCodeNotificationCommand_whenEUAccessCodeNotificationRequestPassed() {

		AccessCodesNotificationRequest request = createAccessCodeNotificationRequest("GBR", "henry@ford.com", true);
		EUAccessCodesNotificationCommand command = service.getEUAccessCodeNotificationCommand(request);
		assertNotNull(command);
		assertTrue(TimedHystrixCommand.class.isAssignableFrom(command.getClass()));
	}
	
	@Test
	public void shouldMapResponseData_toWebWrapper_whenAccessCodesNotificationResponseIsSent() {
		
		EUOSBAccessCodesNotificationResponse osbResponse = createEUOSBAccessCodeNotificationResponse();
		AccessCodesNotificationWebWrapper wrapper = service.mapResponseData_toWebWrapper(osbResponse.getData());
		assertNotNull(wrapper);
		assertTrue(wrapper.isNotified());
	}
	
	@Test
	public void shouldReturnAccessCodeNotificationRequest() {
		
		AccessCodesNotificationRequest request = createAccessCodeNotificationRequest("GBR", "henry@ford.com", true);
		assertNotNull(request);
		assertThat(request.getMarketCode()).isEqualTo("GBR");
		assertThat(request.getEmail()).isEqualTo("henry@ford.com");
		assertThat(request.getOsbSiteTermsRequired()).isTrue();
	}
	
	@Test
	public void shouldReturnResponse_whenBookingCancelled() throws Exception {

		CancelBookingRequest request = createCancelBookingRequest();
		EUOSBCancelBookingResponse osbResponse = createEUOSBCancelBookingResponse();
		
		ArgumentCaptor<CancelBookingRequest> argumentCaptorCancelBookingRequest = ArgumentCaptor
				.forClass(CancelBookingRequest.class);
		doReturn(mockEUCancelBookingCommand).when(service).getEUCancelBookingCommand(request);
		when(mockEUCancelBookingCommand.execute()).thenReturn(osbResponse);
		
		CancelBookingWebWrapper wrapper = service.cancelBooking(bookingReferenceNumber, accessCode,
				osbSiteTermsRequired);
		
		assertNotNull(wrapper);
		assertTrue(wrapper.isBookingCancelled());
		verify(mockEUCancelBookingCommand).execute();
		verify(service).getEUCancelBookingCommand(argumentCaptorCancelBookingRequest.capture());
		assertThat(argumentCaptorCancelBookingRequest.getValue().getBookingReferenceNumber())
				.isEqualTo(bookingReferenceNumber);
		assertThat(argumentCaptorCancelBookingRequest.getValue().getAccessCode()).isEqualTo(accessCode);
		assertThat(argumentCaptorCancelBookingRequest.getValue().isOsbSiteTermsRequired())
				.isEqualTo(osbSiteTermsRequired);
	}

	@Test
	public void shouldReturnEUCancelBookingCommand_whenCancelBookingRequestPassed() {

		CancelBookingRequest request = createCancelBookingRequest();
		EUCancelBookingCommand command = service.getEUCancelBookingCommand(request);
		assertNotNull(command);
		assertTrue(TimedHystrixCommand.class.isAssignableFrom(command.getClass()));
	}
	
	@Test
	public void shouldMapResponseData_toWebWrapper_whenCancelBookingResponseDataIsSent() {
		
		EUOSBCancelBookingResponseData responseData = EUOSBCancelBookingResponseData.builder().isBookingCancelled(false).build();
		CancelBookingWebWrapper wrapper = service.mapResponseData_toWebWrapper(responseData);
		assertThat(wrapper.isBookingCancelled()).isEqualTo(false);
	}
	
	@Test
	public void shouldReturnCancelBookingRequest() {
		
		CancelBookingRequest request = service.createCancelBookingRequest(bookingReferenceNumber, accessCode, osbSiteTermsRequired);
		assertNotNull(request);
		assertThat(request.getBookingReferenceNumber()).isEqualTo(bookingReferenceNumber);
		assertThat(request.getAccessCode()).isEqualTo(accessCode);
		assertThat(request.isOsbSiteTermsRequired()).isEqualTo(osbSiteTermsRequired);
	}
	
	@Test
	public void shouldReturnGetBookingsResponse_whenGetBookingsIsCalled(){
		
		EUOSBGetBookingsData euBookingData = EUOSBGetBookingsData.builder().build();
		EUOSBBookedServicesResponse response = EUOSBBookedServicesResponse.builder()
				.data(euBookingData)
				.build();
		
		GetBookingsRequest request = GetBookingsRequest.builder()
									 .email(email)
									 .accessCode(accessCode)
									 .build();
		
		EUGetBookingsCommand command = mock(EUGetBookingsCommand.class);
		doReturn(command).when(service).getEUGetBookingsCommand(request);

		when(command.execute()).thenReturn(response);
		
		service.getBookings(accessCode, email);
		
		verify(command).execute();
		verify(service).getEUGetBookingsCommand(request);
		verify(service).convertGetBookingsToMSLResponse(response.getData());
	}
	
	@Test
	public void shouldReturnNull_when_passedNullEUOsbDealerProfile(){
		DealerProfile dealerProfile = service.convertToDealerProfile(null);
		assertThat(dealerProfile).isNull();
	}
	
	@Test
	public void shouldReturnDelearProfile_whenPassedEUOsbDealerProfile(){
		EUOSBDealerProfileResponse euosbDealerProfileResponse = getEUOsbDealerProfile();
		
		DealerProfile dealerProfile = service.convertToDealerProfile(euosbDealerProfileResponse);
		
		assertThat(dealerProfile).isNotNull();
		assertThat(dealerProfile.getStreet()).isEqualTo("street");
		assertThat(dealerProfile.getDealerName()).isEqualTo("dealerName");
		assertThat(dealerProfile.getDealerCode()).isEqualTo("dealerCode");
		assertThat(dealerProfile.getDistrict()).isEqualTo("district");
		assertThat(dealerProfile.getPhone()).isEqualTo("phone");
		assertThat(dealerProfile.getTown()).isEqualTo("town");
		assertThat(dealerProfile.getCountry()).isEqualTo("country");
		assertThat(dealerProfile.getPostalCode()).isEqualTo("postalCode");
		assertThat(dealerProfile.getEmail()).isEqualTo("email");
		assertThat(dealerProfile.getOpeningHours().get("monday")).isEqualTo("7:00");
		assertThat(dealerProfile.getOpeningHours().get("tuesday")).isEqualTo("8:00");
		assertThat(dealerProfile.getOpeningHours().get("wednesday")).isEqualTo("");
		assertThat(dealerProfile.getOpeningHours().get("thursday")).isEqualTo("9:00");
		assertThat(dealerProfile.getOpeningHours().get("friday")).isEqualTo("");
		assertThat(dealerProfile.getOpeningHours().get("saturday")).isEqualTo("");
		assertThat(dealerProfile.getOpeningHours().get("sunday")).isEqualTo("10:00");
	}

	@Test
	public void shouldReturnNull_when_EUOsbOldServicesIsNull(){
		List<OldServicesWeb> oldServices = service.convertToOldServicesWeb(null);
		assertThat(oldServices).isNull();
	}
	
	@Test
	public void shouldReturnOldServices_when_passedEUOsbOldServices() {
		String serviceId = "service-id";
		BigDecimal priceAfterDiscount = new BigDecimal("10.5");
		BigDecimal discountPrice = new BigDecimal("30.5");
		BigDecimal price = new BigDecimal("30.5");
		String subType = "sub-type";
		Long discountPercentage = 20L;
		String name = "name";
		String description = "description";

		List<EUOSBOldServiceResponse> euOldServices = new ArrayList<>();
		EUOSBOldServiceResponse euOSBOldServiceResponse = EUOSBOldServiceResponse.builder()
				.serviceId(serviceId)
				.priceAfterDiscount(priceAfterDiscount)
				.discountPrice(discountPrice)
				.price(price)
				.subType(subType)
				.discountPercentage(discountPercentage)
				.name(name)
				.description(description)
				.build();
		euOldServices.add(euOSBOldServiceResponse);

		List<OldServicesWeb> expectedOldServicesWeb = new ArrayList<>();
		OldServicesWeb oldServicesWeb = OldServicesWeb.builder()
				.serviceId(serviceId)
				.priceAfterDiscount(priceAfterDiscount)
				.discountPrice(discountPrice)
				.price(price)
				.subType(subType)
				.discountPercentage(discountPercentage)
				.name(name)
				.description(description)
				.build();
		expectedOldServicesWeb.add(oldServicesWeb);

		List<OldServicesWeb> actualOldServicesWeb = service.convertToOldServicesWeb(euOldServices);
		assertThat(actualOldServicesWeb).isEqualTo(expectedOldServicesWeb);
	}
	
	@Test
	public void shouldReturnNull_when_EUAdditionalServicesIsNull(){
		List<AdditionalServicesWeb> actualAdditionalServicesWeb = service.convertToAdditionalServicesWeb(null);
		assertThat(actualAdditionalServicesWeb).isNull();
	}
	
	@Test
	public void shouldReturnAdditioinalServicesWebList_when_passedEUOSBAdditionalServices(){
		String serviceId = "service-id";
		BigDecimal price = new BigDecimal("10.3");
		boolean selected = true;
		String name = "name";
		String description = "description";
		BigDecimal priceAfterDiscount = new BigDecimal("10.3");
		BigDecimal discountPrice = new BigDecimal("10.3");
		Long discountPercentage=10l;

		List<EUOSBAdditionalServiceResponse> euAdditionalServices = new ArrayList<>();
		EUOSBAdditionalServiceResponse euOSBAdditionalServiceResponse = EUOSBAdditionalServiceResponse.builder()
				.serviceId(serviceId)
				.priceAfterDiscount(priceAfterDiscount)
				.discountPrice(discountPrice)
				.price(price)
				.discountPercentage(discountPercentage)
				.selected(selected)
				.name(name)
				.description(description)
				.build();
		euAdditionalServices.add(euOSBAdditionalServiceResponse);
		
		List<AdditionalServicesWeb> expectedAdditionalServicesWeb = new ArrayList<>();
		AdditionalServicesWeb additionalServicesWeb = AdditionalServicesWeb.builder()
				.serviceId(serviceId)
				.priceAfterDiscount(priceAfterDiscount)
				.discountPrice(discountPrice)
				.price(price)
				.discountPercentage(discountPercentage)
				.selected(selected)
				.name(name)
				.description(description)
				.build();
		expectedAdditionalServicesWeb.add(additionalServicesWeb);
		List<AdditionalServicesWeb> actualAdditionalServicesWeb = service.convertToAdditionalServicesWeb(euAdditionalServices);
		
		assertThat(actualAdditionalServicesWeb).isEqualTo(expectedAdditionalServicesWeb);
	}
	
	@Test
	public void shouldReturnNull_when_EUVehicleDetailsIsNull(){
		VehicleDetailsWeb vehicleDetailsWeb = service.convertToVehicleDetailsWeb(null);
		assertThat(vehicleDetailsWeb).isNull();
	}
	
	@Test
	public void shoudlReturnVehicleDetailsWeb_whenEUVehicleDetailsIsPassed() {
		EUOSBVehicleDetailsResponse euVehicleDetails = EUOSBVehicleDetailsResponse.builder()
				.engine("engine")
				.registrationNumber("registrationNumber")
				.color("color")
				.transmission("transmission")
				.vehicleLineCode("vehicleLineCode")
				.mileageInMiles("mileageInMiles")
				.bodyStyle("bodyStyle")
				.fuelType("fuelType")
				.mileageInKm("mileageInKm")
				.modelName("modelName")
				.version("version")
				.vin("vin")
				.buildDate("buildDate")
				.transmissionType("transmissionType")
				.build();
		
		VehicleDetailsWeb vehicleDetailsWeb = service.convertToVehicleDetailsWeb(euVehicleDetails);
		
		assertThat(vehicleDetailsWeb.getEngine()).isEqualTo("engine");
		assertThat(vehicleDetailsWeb.getRegistrationNumber()).isEqualTo("registrationNumber");
		assertThat(vehicleDetailsWeb.getColor()).isEqualTo("color");
		assertThat(vehicleDetailsWeb.getTransmission()).isEqualTo("transmission");
		assertThat(vehicleDetailsWeb.getVehicleLineCode()).isEqualTo("vehicleLineCode");
		assertThat(vehicleDetailsWeb.getMileageInMiles()).isEqualTo("mileageInMiles");
		assertThat(vehicleDetailsWeb.getBodyStyle()).isEqualTo("bodyStyle");
		assertThat(vehicleDetailsWeb.getFuelType()).isEqualTo("fuelType");
		assertThat(vehicleDetailsWeb.getMileageInKm()).isEqualTo("mileageInKm");
		assertThat(vehicleDetailsWeb.getModelName()).isEqualTo("modelName");
		assertThat(vehicleDetailsWeb.getVersion()).isEqualTo("version");
		assertThat(vehicleDetailsWeb.getVin()).isEqualTo("vin");
		assertThat(vehicleDetailsWeb.getBuildDate()).isEqualTo("buildDate");
		assertThat(vehicleDetailsWeb.getTransmissionType()).isEqualTo("transmissionType");
	}
	
	@Test
	public void shouldReturnNull_when_EUMainServiceIsNull(){
		MainServicesWeb mainServicesWeb = service.convertToMainServicesWeb(null);
		assertThat(mainServicesWeb).isNull();
	}
	
	@Test
	public void shoudlReturnMainServicesWeb_whenEUMainServiceIsPassed() {
		BigDecimal price = new BigDecimal("10.3");
		BigDecimal priceAfterDiscount = new BigDecimal("10.3");
		BigDecimal discountPrice = new BigDecimal("10.3");
		Long discountPercentage=10l;
		EUOSBMainServiceResponse euMainService = EUOSBMainServiceResponse.builder()
				.serviceId("serviceId")
				.priceAfterDiscount(priceAfterDiscount)
				.discountPrice(discountPrice)
				.price(price)
				.discountPercentage(discountPercentage)
				.subType("subType")
				.name("name")
				.description("description")
				.applicationInformation("applicationInformation")
				.build();
		
		MainServicesWeb mainServicesWeb = service.convertToMainServicesWeb(euMainService);
		
		assertThat(mainServicesWeb.getServiceId()).isEqualTo("serviceId");
		assertThat(mainServicesWeb.getPriceAfterDiscount()).isEqualTo(priceAfterDiscount);
		assertThat(mainServicesWeb.getDiscountPrice()).isEqualTo(discountPrice);
		assertThat(mainServicesWeb.getPrice()).isEqualTo(price);
		assertThat(mainServicesWeb.getDiscountPercentage()).isEqualTo(discountPercentage);
		assertThat(mainServicesWeb.getSubType()).isEqualTo("subType");
		assertThat(mainServicesWeb.getName()).isEqualTo("name");
		assertThat(mainServicesWeb.getDescription()).isEqualTo("description");
		assertThat(mainServicesWeb.getApplicationInformation()).isEqualTo("applicationInformation");
	}
	
	@Test
	public void shouldReturnNull_whenEUVoucherCodesIsNull() {
		List<ServiceVoucher> serviceVoucher = service.convertToServiceVoucher(null);
		assertThat(serviceVoucher).isNull();
	}
	
	@Test
	public void shouldReturnServiceVoucherList_whenEUVoucherCodesIsPassed() {
		 String voucherCodeDescription = "voucherCodeDescription";
		 BigDecimal voucherAmount = new BigDecimal("10.4");
		 Long voucherPercentage = 20L;
		 String voucherCode = "voucherCode";

		List<EUOSBServiceVoucher> euVoucherCodes = new ArrayList<>();
		EUOSBServiceVoucher euOSBServiceVoucher = EUOSBServiceVoucher.builder()
				.voucherCodeDescription(voucherCodeDescription)
				.voucherAmount(voucherAmount)
				.voucherPercentage(voucherPercentage)
				.voucherCode(voucherCode)
				.build();
		euVoucherCodes.add(euOSBServiceVoucher);
		
		List<ServiceVoucher> expectedServiceVouchers = new ArrayList<>();
		ServiceVoucher serviceVoucher = ServiceVoucher.builder()
				.description(voucherCodeDescription)
				.amount(voucherAmount)
				.percentage(voucherPercentage)
				.code(voucherCode)
				.build();
		expectedServiceVouchers.add(serviceVoucher);		
				
		List<ServiceVoucher> actualServiceVouchers =  service.convertToServiceVoucher(euVoucherCodes);
		assertThat(actualServiceVouchers).isEqualTo(expectedServiceVouchers);
	}
	
	@Test
	public void shouldReturnNull_when_EUCustomerIsNull(){
		CustomerWeb actualCustomer = service.convertToCustomerWeb(null);
		assertThat(actualCustomer).isEqualTo(null);
	}
	
	@Test
	public void shouldReturnCustomerWeb_when_EUcustomerIspassed(){
		 String lastName = "lastname";
		 String title = "title";
		 String contactNumber = "contactnumber";
		 String firstName = "firstname";
		 String email = "email";
		 String phone = "phone";
		 
		EUOSBCustomerResponse euCustomer =  EUOSBCustomerResponse.builder()
				.lastName(lastName)
				.title(title)
				.contactNumber(contactNumber)
				.firstName(firstName)
				.email(email)
				.phone(phone)
				.build();
		CustomerWeb expectedCustomerWeb = CustomerWeb.builder()
				.lastName(lastName)
				.title(title)
				.contactNumber(contactNumber)
				.firstName(firstName)
				.email(email)
				.phone(phone)
				.build();
				
		CustomerWeb actualCustomer = service.convertToCustomerWeb(euCustomer);
		assertThat(actualCustomer).isEqualTo(expectedCustomerWeb);
	}
	
	@Test
	public void returnNull_when_EUOsbBookingDataIsNull(){
		GetBookingsWebWrapper actualBookingData = service.convertGetBookingsToMSLResponse(null);
		assertThat(actualBookingData).isEqualTo(null);
	}
	
	@Test
	public void returnGetBookingWebWrapper_when_EUOsbBookingDataIsPassed(){
		String appTime = "appTime";
		boolean previousBooking = true;
		String bookingRefNumber = "bookingRefNumber";
		String comments = "comments";
		String vehicleLineDescription = "vehicleLineDescription";
		EUOSBGetBookingsData data = EUOSBGetBookingsData.builder()
				.appointmentTime(appTime)
				.previousBooking(previousBooking)
				.comments(comments)
				.vehicleLineDescription(vehicleLineDescription)
				.bookingReferenceNumber(bookingRefNumber)
				.build();
		GetBookingsWebWrapper wrapper = service.convertGetBookingsToMSLResponse(data);
		
		assertThat(wrapper.getBookings().get(0).getAppointmentTime()).isNotNull();
		Assertions.assertThat(wrapper.getBookings().get(0).getDealer()).isNull();
		assertThat(wrapper.getBookings().get(0).isPreviousBooking()).isEqualTo(previousBooking);
		assertThat(wrapper.getBookings().get(0).getBookingReferenceNumber()).isEqualTo(bookingRefNumber);
		assertThat(wrapper.getBookings().get(0).getComments()).isEqualTo(comments);
		Assertions.assertThat(wrapper.getBookings().get(0).getOldServices()).isNull();
		Assertions.assertThat(wrapper.getBookings().get(0).getAdditionalServices()).isNull();
		Assertions.assertThat(wrapper.getBookings().get(0).getVehicleDetails()).isNull();
		Assertions.assertThat(wrapper.getBookings().get(0).getMainService()).isNull();
		Assertions.assertThat(wrapper.getBookings().get(0).getVoucherCodes()).isNull();
		assertThat(wrapper.getBookings().get(0).getVehicleLineDescription()).isEqualTo(vehicleLineDescription);
		Assertions.assertThat(wrapper.getBookings().get(0).getCustomer()).isNull();
		
		verify(service).convertToCustomerWeb(data.getCustomer());
		verify(service).convertToServiceVoucher(data.getVoucherCodes());
		verify(service).convertToMainServicesWeb(data.getMainService());
		verify(service).convertToVehicleDetailsWeb(data.getVehicleDetails());
		verify(service).convertToAdditionalServicesWeb(data.getAdditionalServices());
		verify(service).convertToOldServicesWeb(data.getOldServices());
		verify(service).convertToDealerProfile(data.getDealerProfile());
	}
	
	@Test
	public void shouldMapWebRequest_toOSBRequest() {
		
		CreateBookingWebRequest webRequest = createCreateBookingWebRequest();
		CreateBookingOSBRequest actualOSBRequest = service.mapWebRequestToOSBRequest(webRequest);
		CreateBookingOSBRequest expectedOSBRequest = createCreateBookingOSBRequest();
		assertThat(expectedOSBRequest).isEqualTo(actualOSBRequest);
	}
	
	@Test
	public void shouldMapWebRequest_toOSBRequest_additionalService_shouldBeNull() {
		
		CreateBookingWebRequest webRequest = createCreateBookingWebRequest();
		webRequest.setAdditionalServices(null);
		CreateBookingOSBRequest actualOSBRequest = service.mapWebRequestToOSBRequest(webRequest);
		CreateBookingOSBRequest expectedOSBRequest = createCreateBookingOSBRequest();
		expectedOSBRequest.setNewAdditionalServices(null);
		assertNotNull(actualOSBRequest.getNewAdditionalServices());
		assertTrue(actualOSBRequest.getNewAdditionalServices().isEmpty());
	}
	
	@Test
	public void shouldMapWebRequest_toOSBRequest_additionalService_shouldBeEmpty() {
		
		CreateBookingWebRequest webRequest = createCreateBookingWebRequest();
		webRequest.setAdditionalServices(new ArrayList<>());
		CreateBookingOSBRequest actualOSBRequest = service.mapWebRequestToOSBRequest(webRequest);
		CreateBookingOSBRequest expectedOSBRequest = createCreateBookingOSBRequest();
		expectedOSBRequest.setNewAdditionalServices(new ArrayList<>());
		assertThat(expectedOSBRequest).isEqualTo(actualOSBRequest);
		assertNotNull(actualOSBRequest.getNewAdditionalServices());
		assertTrue(actualOSBRequest.getNewAdditionalServices().isEmpty());
	}
	
	@Test 
	public void shouldMapWebRequest_toOSBRequest_customer_shouldBeNull() {
		
		CreateBookingWebRequest webRequest = createCreateBookingWebRequest();
		webRequest.setCustomer(null);
		CreateBookingOSBRequest actualOSBRequest = service.mapWebRequestToOSBRequest(webRequest);
		CreateBookingOSBRequest expectedOSBRequest = createCreateBookingOSBRequest();
		expectedOSBRequest.setCustomer(null);
		assertThat(expectedOSBRequest).isEqualTo(actualOSBRequest);
		assertNull(actualOSBRequest.getCustomer());
	}
	
	private CreateBookingOSBRequest createCreateBookingOSBRequest() {
		
		List<String> voucherCodes = Arrays.asList("v1", "v2");
		List<String> repairServices = Arrays.asList("r1", "r2");
		List<OSBCreateBookingAdditionalService> additionalServices = new ArrayList<>();
		additionalServices.add(OSBCreateBookingAdditionalService.builder()
				.additionalServiceId("a1")
				.build());
		additionalServices.add(OSBCreateBookingAdditionalService.builder()
				.additionalServiceId("a2")
				.build());
		
		return CreateBookingOSBRequest.builder()
				.locale("locale")
				.marketCode("market-code")
				.dealerCode("dealer-code")
				.modelName("model-name")
				.buildYear("build-year")
				.vin("vin")
				.registrationNumber("registration-number")
				.voucherCodes(voucherCodes)
				.serviceType("service-type")
				.osbSiteTermsRequired(true)
				.comments("comments")
				.appointmentTime("appointment-time")
				.mainServiceId("main-service-id")
				.valueServiceId("value-service-id")
				.repairServices(repairServices)
				.newAdditionalServices(additionalServices)
				.customer(EUOSBCustomer.builder()
						.title("title")
						.firstName("first-name")
						.lastName("last-name")
						.contactNumber("contact-number")
						.email("email")
						.build())
				.build();
	}
	
	private CreateBookingWebRequest createCreateBookingWebRequest() {
		
		List<String> voucherCodes = Arrays.asList("v1", "v2");
		List<String> repairServices = Arrays.asList("r1", "r2");
		List<String> additionalServices = Arrays.asList("a1", "a2");
		
		return CreateBookingWebRequest.builder()
				.locale("locale")
				.marketCode("market-code")
				.dealerCode("dealer-code")
				.modelName("model-name")
				.buildYear("build-year")
				.vin("vin")
				.registrationNumber("registration-number")
				.voucherCodes(voucherCodes)
				.serviceType("service-type")
				.osbSiteTermsRequired(true)
				.comments("comments")
				.appointmentTime("appointment-time")
				.mainServiceId("main-service-id")
				.valueServiceId("value-service-id")
				.repairServices(repairServices)
				.additionalServices(additionalServices)
				.customer(EUWebCustomer.builder()
						.title("title")
						.firstName("first-name")
						.lastName("last-name")
						.contactNumber("contact-number")
						.email("email")
						.build())
				.build();
	}
	
	private AccessCodesNotificationRequest createAccessCodeNotificationRequest(String marketCode, String email, Boolean osbSiteTermsRequired) {
		
		AccessCodesNotificationRequest request = AccessCodesNotificationRequest.builder().build();
		if (marketCode != null) {
			request.setMarketCode(marketCode);
		}
		if (email != null) {
			request.setEmail(email);
		}
		if (osbSiteTermsRequired != null) {
			request.setOsbSiteTermsRequired(osbSiteTermsRequired);
		}
		return request;
	}
	
	private EUOSBAccessCodesNotificationResponse createEUOSBAccessCodeNotificationResponse() {
		
		EUOSBAccessCodesNotificationResponse osbResponse = EUOSBAccessCodesNotificationResponse.builder().build();
		EUOSBAccessCodesNotificationResponseData responseData = EUOSBAccessCodesNotificationResponseData.builder().isReminderSent(true).build();
		osbResponse.setData(responseData);
		return osbResponse;
	}
	
	private CancelBookingRequest createCancelBookingRequest() {
		CancelBookingRequest request = CancelBookingRequest.builder()
				.bookingReferenceNumber(bookingReferenceNumber)
				.accessCode(accessCode)
				.osbSiteTermsRequired(osbSiteTermsRequired)
				.build();
		return request;
	}
	
	private EUOSBCancelBookingResponse createEUOSBCancelBookingResponse() {
		EUOSBCancelBookingResponse osbResponse = EUOSBCancelBookingResponse.builder().build();
		EUOSBCancelBookingResponseData responseData = EUOSBCancelBookingResponseData.builder().isBookingCancelled(true).build();
		osbResponse.setData(responseData);
		return osbResponse;
	}
	
	private EUOSBDealerProfileResponse getEUOsbDealerProfile() {
		EUOSBDealerProfileResponse euosbDealerProfileResponse = EUOSBDealerProfileResponse.builder()
				.street("street")
				.dealerName("dealerName")
				.dealerCode("dealerCode")
				.district("district")
				.phone("phone")
				.town("town")
				.country("country")
				.postalCode("postalCode")
				.email("email")
				.build();
		Map<String, String> openingHours = euosbDealerProfileResponse.getOpeningHours();
		openingHours.put("monday", "7:00");
		openingHours.put("tuesday", "8:00");
		openingHours.put("thursday", "9:00");
		openingHours.put("sunday", "10:00");
		return euosbDealerProfileResponse;
	}
}
