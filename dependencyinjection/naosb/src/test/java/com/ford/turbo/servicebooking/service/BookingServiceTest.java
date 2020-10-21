package com.ford.turbo.servicebooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.AdditionalServiceRequest;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.OldServiceBooking;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.command.eu.EUCreateBookingCommand;
import com.ford.turbo.servicebooking.exception.BookingAlreadyExistsException;
import com.ford.turbo.servicebooking.models.osb.BookedAdditionalService;
import com.ford.turbo.servicebooking.models.osb.Dealer;
import com.ford.turbo.servicebooking.models.osb.DealerProfile;
import com.ford.turbo.servicebooking.models.osb.OSBBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCBookingCreateBookingPostString;
import com.ford.turbo.servicebooking.models.osb.OSBOVService;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.models.osb.RetrieveBookingsForOwnerResponse;
import com.ford.turbo.servicebooking.models.osb.SelectedVehicleWithPrice;
import com.ford.turbo.servicebooking.models.osb.TimeAsDate;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBWebError;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;
import com.ford.turbo.servicebooking.models.osb.request.EUOSBCustomer;
import com.ford.turbo.servicebooking.models.osb.request.OSBCreateBookingAdditionalService;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceTest {
	private static final String TOTAL_PRICE_AFTER_DISCOUNT = "60.05";
	private static final String TOTAL_PRICE = "65.05";
	private static final String MOT_UNIQUE_ID = "3007";
	private static final String TEST_DEALER_NAME = "TEST_DEALER_NAME";
	private static final String TEST_MAIN_SERVICE_ID = "1234";
	private static final String TEST_ADDITIONAL_SERVICE_ID = "123456";
	private static final String TIME_ZONE_OFFSET = "240";
	private static final String TEST_CUSTOMER_BOOKING_REF = "TestBookingRef";
	private static final String CUSTOMER_ANNOTATION = "CustomerAnnotation";
	private static final String VALUE_UNIQUE_ID = "3006";
	private static final String REPAIR_UNIQUE_ID = "3001";
	
	@Spy
	@InjectMocks
	private EUBookingService service;
	
	@Mock
	private EUListDetailInformationService mockListService;
	
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	
	@Mock
	private CredentialsSource mockCredentialsSource;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	
	@Mock
	private UserProfileService mockUserProfileService;
	
	@Captor
	private ArgumentCaptor<HttpEntity<OSBCBookingCreateBookingPostString>> createBookingCaptor;
	
	private String marketCode;
	private String opusConsumerId;

	@Test
	public void shouldReturn_EUCreateBookingCommand() {
		
		CreateBookingOSBRequest mockRequest = mock(CreateBookingOSBRequest.class);
		EUCreateBookingCommand command = service.createEUCreateBookingCommand(mockRequest);
		assertTrue(command instanceof TimedHystrixCommand);
	}
	
	@Test
	public void should_List_Service_Bookings_WithoutOldServices() {
		mockListServiceBookings(mockListBookingsResponseWithOutOldServices());
		
		BookedServiceResponse response = service.listServiceBookings(opusConsumerId, marketCode, Optional.empty());
		
		assertBasicDetailsOfBookedResponse(response);
		assertThat(response.getOldServices().size()).isEqualTo(0);
	}
	
	@Test
	public void should_List_Service_Bookings_With_MOTBookings() {
		mockListServiceBookings(mockListBookingsResponseWithMOTServices());
		
		BookedServiceResponse response = service.listServiceBookings(opusConsumerId, marketCode, Optional.empty());
		
		assertBasicDetailsOfBookedResponse(response);
		
		OldServiceBooking motOldServiceResponse = response.getOldServices().get(0);
		assertThat(motOldServiceResponse.getServiceId()).isEqualTo(MOT_UNIQUE_ID+":MOT");
		assertThat(motOldServiceResponse.getPrice()).isEqualTo("35.00");
		assertThat(motOldServiceResponse.getPriceAfterDiscount()).isEqualTo("30.00");
		assertThat(motOldServiceResponse.getName()).isEqualTo("MOT with Service");
		Assertions.assertThat(motOldServiceResponse.getSubType()).isEqualTo(OldServiceType.MOT);
	}
	
	@Test
	public void should_List_Service_Bookings_With_ValueBookings() {
		mockListServiceBookings(mockListBookingsResponseWithValueServices());
		
		BookedServiceResponse response = service.listServiceBookings(opusConsumerId, marketCode, Optional.empty());
		
		assertBasicDetailsOfBookedResponse(response);

		OldServiceBooking valueOldServiceResponse = response.getOldServices().get(0);//get(1) as we will get general booking in '0'
		assertThat(valueOldServiceResponse.getServiceId()).isEqualTo(VALUE_UNIQUE_ID+":VALUE");
		Assertions.assertThat(valueOldServiceResponse.getSubType()).isEqualTo(OldServiceType.VALUE);
		assertThat(valueOldServiceResponse.getPrice()).isEqualTo("115.00");
		assertThat(valueOldServiceResponse.getName()).isEqualTo("Major Service");
		assertThat(valueOldServiceResponse.getPriceAfterDiscount()).isEqualTo("110.00");
	}

	private void assertBasicDetailsOfBookedResponse(BookedServiceResponse response) {
		assertThat(response.getBookingCustomerRefNum()).isEqualTo(TEST_CUSTOMER_BOOKING_REF);
		assertThat(response.getDealerProfile().getDealerName()).isEqualTo(TEST_DEALER_NAME);
		assertThat(response.getAdditionalServices().get(0).getServiceId()).isEqualTo(TEST_ADDITIONAL_SERVICE_ID);
		assertThat(response.getTotalPrice()).isEqualTo(TOTAL_PRICE);
		assertThat(response.getTotalPriceAfterDiscount()).isEqualTo(TOTAL_PRICE_AFTER_DISCOUNT);
	}
	
	@Test
	public void should_List_Service_Bookings_With_RepairBookings() {
		mockListServiceBookings(mockListBookingsResponseWithRepairServices());
		
		BookedServiceResponse response = service.listServiceBookings(opusConsumerId, marketCode, Optional.empty());
		
		assertThat(response.getBookingCustomerRefNum()).isEqualTo(TEST_CUSTOMER_BOOKING_REF);
		assertThat(response.getDealerProfile().getDealerName()).isEqualTo(TEST_DEALER_NAME);
		assertThat(response.getTotalPrice()).isEqualTo(TOTAL_PRICE);
		assertThat(response.getTotalPriceAfterDiscount()).isEqualTo(TOTAL_PRICE_AFTER_DISCOUNT);

		OldServiceBooking repairOldServiceResponse = response.getOldServices().get(0);//get(1) as we will get general booking in '0'
		assertThat(repairOldServiceResponse.getServiceId()).isEqualTo(REPAIR_UNIQUE_ID+":REPAIR");
		Assertions.assertThat(repairOldServiceResponse.getSubType()).isEqualTo(OldServiceType.REPAIR);
		assertThat(repairOldServiceResponse.getPrice()).isEqualTo("35.00");
		assertThat(repairOldServiceResponse.getPriceAfterDiscount()).isEqualTo("30.00");
		assertThat(repairOldServiceResponse.getName()).isEqualTo("Repair with Service");
	}
	
	@Test
    public void should_perform_all_steps_required_for_create_booking_Old_Services() throws Exception {
		UserProfile mockUserProfile = mockUserProfile();
		CreateBookingOSBRequest expectedOSBRequest = newWebOsbBooking();
		expectedOSBRequest.setMotServiceId("3019");
		expectedOSBRequest.setValueServiceId("3006");
		List<String> repairServices = Arrays.asList("3006");
		expectedOSBRequest.setRepairServices(repairServices);
		List<String> expectedVoucherCodes = Arrays.asList("VC001");
		expectedOSBRequest.setVoucherCodes(expectedVoucherCodes);
		EUOSBCustomer customer = EUOSBCustomer.builder()
				.firstName(mockUserProfile.getFirstName())
				.lastName(mockUserProfile.getLastName())
				.title(mockUserProfile.getTitle())
				.contactNumber(mockUserProfile.getPhoneNumber())
				.guid("00000")
				.email(mockUserProfile.getUserId())
				.build();
		expectedOSBRequest.setCustomer(customer);
		
	    	String voucherCode = "VC001";
	    	CreateBookingRequest bookingRequest = newMobileOsbBooking();
	    	bookingRequest.setOldServices(Arrays.asList("3019:"+OldServiceType.MOT,"3006:"+OldServiceType.VALUE,"3006:"+OldServiceType.REPAIR));
	    	bookingRequest.setVoucherCode(voucherCode);
	    
	    	doReturn(mockUserProfile).when(mockUserProfileService).getUserProfile("auth_token", "appId");
	    	
	    	EUCreateBookingCommand mockCommand = mock(EUCreateBookingCommand.class);
	    	doReturn(mockCommand).when(service).createEUCreateBookingCommand(any());
	    	EUOSBCreateBookingResponse response = new EUOSBCreateBookingResponse();
	    CreateBookingWebWrapper wrapper = CreateBookingWebWrapper.builder()
	    		.bookingReferenceNumber("ford-123-booking-ref")
	    		.build();
	    response.setData(wrapper);
	    	when(mockCommand.execute()).thenReturn(response);
	    	
	    	String responseString = service.createBooking(bookingRequest, "appId", "auth_token");
	    	assertThat(responseString).isEqualTo("ford-123-booking-ref");
	    	ArgumentCaptor<CreateBookingOSBRequest> argumentCaptorCreateBookingWebRequest = ArgumentCaptor.forClass(CreateBookingOSBRequest.class);
	    	verify(service).createEUCreateBookingCommand(argumentCaptorCreateBookingWebRequest.capture());
	    	assertEquals(expectedOSBRequest, argumentCaptorCreateBookingWebRequest.getValue());
    }
	
	
	
	@Test
	public void shouldMapMobileRequest_toOSBRequest() {
		UserProfile mockUserProfile = mockUserProfile();
		CreateBookingOSBRequest expectedOSBRequest = newWebOsbBooking();
		expectedOSBRequest.setMotServiceId("3019");
		expectedOSBRequest.setValueServiceId("3006");
		List<String> repairServices = Arrays.asList("3006");
		expectedOSBRequest.setRepairServices(repairServices);
		List<String> expectedVoucherCodes = Arrays.asList("VC001");
		expectedOSBRequest.setVoucherCodes(expectedVoucherCodes);
		EUOSBCustomer customer = EUOSBCustomer.builder()
				.firstName(mockUserProfile.getFirstName())
				.lastName(mockUserProfile.getLastName())
				.title(mockUserProfile.getTitle())
				.contactNumber(mockUserProfile.getPhoneNumber())
				.guid("00000")
				.email(mockUserProfile.getUserId())
				.build();
		expectedOSBRequest.setCustomer(customer);
		
	    	String voucherCode = "VC001";
	    	CreateBookingRequest bookingRequest = newMobileOsbBooking();
	    	bookingRequest.setOldServices(Arrays.asList("3019:"+OldServiceType.MOT,"3006:"+OldServiceType.VALUE,"3006:"+OldServiceType.REPAIR));
	    	bookingRequest.setVoucherCode(voucherCode);
	    	CreateBookingOSBRequest actualOSBRequest = service.getCreateBookingOSBRequest(bookingRequest, mockUserProfile);
	    	
	    	assertEquals(expectedOSBRequest, actualOSBRequest);
	}
	
	@Test
	public void shouldMapMobileRequest_toOSBRequest_withOldServicesNull() {
		
		UserProfile mockUserProfile = mockUserProfile();
		CreateBookingOSBRequest expectedOSBRequest = newWebOsbBooking();
		List<String> repairServices = new ArrayList<>();
		expectedOSBRequest.setRepairServices(repairServices);
		List<String> expectedVoucherCodes = Arrays.asList("VC001");
		expectedOSBRequest.setVoucherCodes(expectedVoucherCodes);
		EUOSBCustomer customer = EUOSBCustomer.builder()
				.firstName(mockUserProfile.getFirstName())
				.lastName(mockUserProfile.getLastName())
				.title(mockUserProfile.getTitle())
				.contactNumber(mockUserProfile.getPhoneNumber())
				.guid("00000")
				.email(mockUserProfile.getUserId())
				.build();
		expectedOSBRequest.setCustomer(customer);
		
	    	String voucherCode = "VC001";
	    	CreateBookingRequest bookingRequest = newMobileOsbBooking();
	    	bookingRequest.setVoucherCode(voucherCode);
	    	CreateBookingOSBRequest actualOSBRequest = service.getCreateBookingOSBRequest(bookingRequest, mockUserProfile);
	    	
	    	assertEquals(expectedOSBRequest, actualOSBRequest);
	}
	
	@Test
	public void shouldMapMobileRequest_toOSBRequest_withMainServiceIdNull() {
		
		UserProfile mockUserProfile = mockUserProfile();
		CreateBookingOSBRequest expectedOSBRequest = newWebOsbBooking();
		List<String> repairServices = new ArrayList<>();
		expectedOSBRequest.setRepairServices(repairServices);
		List<String> expectedVoucherCodes = Arrays.asList("VC001");
		expectedOSBRequest.setVoucherCodes(expectedVoucherCodes);
		EUOSBCustomer customer = EUOSBCustomer.builder()
				.firstName(mockUserProfile.getFirstName())
				.lastName(mockUserProfile.getLastName())
				.title(mockUserProfile.getTitle())
				.contactNumber(mockUserProfile.getPhoneNumber())
				.guid("00000")
				.email(mockUserProfile.getUserId())
				.build();
		expectedOSBRequest.setCustomer(customer);
		expectedOSBRequest.setMainServiceId("66666");
		
	    	String voucherCode = "VC001";
	    	CreateBookingRequest bookingRequest = newMobileOsbBooking();
	    	bookingRequest.setMainServiceId(null);
	    	bookingRequest.setVoucherCode(voucherCode);
	    	CreateBookingOSBRequest actualOSBRequest = service.getCreateBookingOSBRequest(bookingRequest, mockUserProfile);
	    	
	    	assertEquals(expectedOSBRequest, actualOSBRequest);
	}
	
	@Test(expected = BookingAlreadyExistsException.class)
	public void shouldThrowBookingAlreadyExistsException_whenBackend_IsCalled() throws Exception {
		CreateBookingRequest request = CreateBookingRequest.builder()
				.dealerCode("dealerCode")
				.build();
		mockUserProfile();
		EUOSBCreateBookingResponse osbResponse = new EUOSBCreateBookingResponse();
		osbResponse.setError(EUOSBWebError.builder().code("OSB_VIN_EXISTS").build());
		doReturn(CreateBookingOSBRequest.builder().build()).when(service).getCreateBookingOSBRequest(any(), any());
		EUCreateBookingCommand command = mock(EUCreateBookingCommand.class);
		doReturn(command).when(service).createEUCreateBookingCommand(any());
		when(command.execute()).thenReturn(osbResponse);
		
		service.createBooking(request, "appId", "authToken");
		
	}
	
	@Test(expected = BadRequestException.class)
	public void shouldThrowBadRequestException_whenBackend_IsCalled() throws Exception {
		CreateBookingRequest request = CreateBookingRequest.builder()
				.dealerCode("dealerCode")
				.build();
		mockUserProfile();
		EUOSBCreateBookingResponse osbResponse = new EUOSBCreateBookingResponse();
		osbResponse.setError(EUOSBWebError.builder().statusCode("400").build());
		doReturn(CreateBookingOSBRequest.builder().build()).when(service).getCreateBookingOSBRequest(any(), any());
		EUCreateBookingCommand command = mock(EUCreateBookingCommand.class);
		doReturn(command).when(service).createEUCreateBookingCommand(any());
		when(command.execute()).thenReturn(osbResponse);
		
		service.createBooking(request, "appId", "authToken");
		
	}
	private CreateBookingRequest newMobileOsbBooking() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setCountry(CountryCode.GBR);
        request.setCustomerAnnotation("My comments");
        request.setDealerCode("46396AA");
        request.setVin("WF0EXXGBBEAG33487");
        request.setAdditionalServices(Arrays.asList(new AdditionalServiceRequest("6", "additional service comments")));
        request.setMainServiceId("55009529");
        request.setLanguage(LanguageCode.EN);
        request.setRegion(RegionCode.UK);
        ZonedDateTime appointmentTime = ZonedDateTime.now().plusDays(21);
        appointmentTime.minusSeconds(appointmentTime.getSecond());
        appointmentTime.minusMinutes(appointmentTime.getMinute());
		request.setApptTime(appointmentTime);
        request.setMileage("10000");
        return request;
	}
	
	private CreateBookingOSBRequest newWebOsbBooking() {
        CreateBookingOSBRequest request = new CreateBookingOSBRequest();
        request.setLocale("en-gb");
        request.setMarketCode("GBR");
        request.setComments("My comments");
        request.setDealerCode("46396AA");
        request.setVin("WF0EXXGBBEAG33487");
        OSBCreateBookingAdditionalService additionalService = OSBCreateBookingAdditionalService.builder()
        		.additionalServiceComments("additional service comments")
        		.additionalServiceId("6").build();
        List<OSBCreateBookingAdditionalService> additionalServices = Arrays.asList(additionalService);
        request.setNewAdditionalServices(additionalServices);
        request.setMainServiceId("55009529");
        ZonedDateTime appointmentTime = ZonedDateTime.now().plusDays(21);
        appointmentTime.minusSeconds(appointmentTime.getSecond());
        appointmentTime.minusMinutes(appointmentTime.getMinute());
       
        DateTimeFormatter appointmentTimeFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy'T'HH:mm:ss"); // ("dd-MM-yyyy'T'HH:mm:ss");
		String appointmentTimeString = appointmentTime.format(appointmentTimeFormatter);
		request.setAppointmentTime(appointmentTimeString);
        request.setMileage("10000");
        request.setOsbSiteTermsRequired(true);
        request.setServiceType("50");
        return request;
	}
	 
	private UserProfile mockUserProfile() {
	    	UserProfile userProfile = Mockito.mock(UserProfile.class);
	    	when(userProfile.getLastName()).thenReturn("Test Last Name");
	    	when(userProfile.getFirstName()).thenReturn("First Name");
	    	when(userProfile.getUserId()).thenReturn("userId");
	    	when(userProfile.getTitle()).thenReturn("Test Title");
	    	when(userProfile.getPhoneNumber()).thenReturn("123456789");
	    	
	    	return userProfile;
    }
    
	private void mockListServiceBookings(RetrieveBookingsForOwnerResponse[] response) {
		ResponseEntity<RetrieveBookingsForOwnerResponse[]> responseEntity = new ResponseEntity<RetrieveBookingsForOwnerResponse[]>(response, HttpStatus.OK);
		
		when(mockMutualAuthRestTemplate.exchange(Mockito.contains("/rest/s/c/b/wfg"), eq(GET), any(), eq(RetrieveBookingsForOwnerResponse[].class)))
			.thenReturn(responseEntity);
	}
	
	private RetrieveBookingsForOwnerResponse[] mockListBookingsResponseWithOutOldServices() {
		return createRetrieveBookingsForOwnerResponse(createBookingData());
	}
	
	private RetrieveBookingsForOwnerResponse[] mockListBookingsResponseWithMOTServices() {
		OSBBookingData bookingData = createBookingData();
		OSBOVService[] motJSON = createMOTJSON();
		bookingData.setMotJSON(motJSON);
		return createRetrieveBookingsForOwnerResponse(bookingData);
	}
	
	private RetrieveBookingsForOwnerResponse[] mockListBookingsResponseWithValueServices() {
		OSBBookingData bookingData = createBookingData();
		OSBOVService[] valueServiceJSON = createValueJSON();
		bookingData.setValueServiceJSON(valueServiceJSON);
		return createRetrieveBookingsForOwnerResponse(bookingData);
	}
	
	private RetrieveBookingsForOwnerResponse[] mockListBookingsResponseWithRepairServices() {
		OSBBookingData bookingData = createBookingData();
		OSBOVService[] repairsJSON = createRepairJSON();
		bookingData.setRepairsJSON(repairsJSON);
		return createRetrieveBookingsForOwnerResponse(bookingData);
	}
	
	private RetrieveBookingsForOwnerResponse[] createRetrieveBookingsForOwnerResponse (OSBBookingData bookingData) {
		RetrieveBookingsForOwnerResponse[] responseBodyArray = new RetrieveBookingsForOwnerResponse [1];
		RetrieveBookingsForOwnerResponse bookingResponse = new RetrieveBookingsForOwnerResponse();
		OSBBookingData [] bookingDataArray = new OSBBookingData [1];
		bookingDataArray[0] = bookingData;
		bookingResponse.setData(bookingDataArray);
		bookingResponse.setStatus(HttpStatus.OK.value());
		responseBodyArray[0] = bookingResponse;
		return responseBodyArray;
	}

	private OSBBookingData createBookingData() {
		OSBBookingData bookingData = new OSBBookingData();
		TimeAsDate withinThreeMonths = new TimeAsDate().time(BigDecimal.valueOf(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		withinThreeMonths.setTimezoneOffset(new BigDecimal(TIME_ZONE_OFFSET));
		bookingData.setAppointmentTimeAsDate(withinThreeMonths);
		
        bookingData.setBookingCustomerRefNum(TEST_CUSTOMER_BOOKING_REF);
        bookingData.setCustomerAnnotation(CUSTOMER_ANNOTATION);
        Dealer dealer = new Dealer();
        dealer.setDealerProfile(new DealerProfile().dealerName(TEST_DEALER_NAME));
        
        bookingData.setDealer(dealer);
		bookingData.setMainServiceId(new BigDecimal(TEST_MAIN_SERVICE_ID));
		//Set Additional Service Information
		List<BookedAdditionalService> bookedAdditionalServices = new ArrayList<>();
		BookedAdditionalService bookedAdditionalService = new BookedAdditionalService();
		bookedAdditionalService.setAdditionalServiceId(TEST_ADDITIONAL_SERVICE_ID);
		bookedAdditionalServices.add(bookedAdditionalService);
		bookingData.setBookedAdditionalServices(bookedAdditionalServices);
		bookingData.setTotalPrice(TOTAL_PRICE);
		bookingData.setTotalPriceAfterDiscount(TOTAL_PRICE_AFTER_DISCOUNT);
		return bookingData;
	}

	private OSBOVService[] createMOTJSON() {
		OSBOVService [] motJSON = new OSBOVService [1];
		OSBOVService motService =  new OSBOVService();
		motService.setUniqueId(MOT_UNIQUE_ID);
		motService.setName("MOT with Service");
		motService.setDealerCode("46396AA");
		motService.setLocale("en-gb");
		motService.setMarketCode("GBR");
		motService.setModel("Focus");
		motService.setServiceType(OldServiceType.MOT);
		SelectedVehicleWithPrice selectedVehicle = new SelectedVehicleWithPrice();
		selectedVehicle.setPrice("35.00");
		selectedVehicle.setDescription("If booked together with a Motorcraft Minor or Major service, enjoy an extra \u00c2\u00a35 off Plus earn triple Nectar Points on the MOT (6 points per \u00c2\u00a31 spent)");
		selectedVehicle.setPriceAfterDiscount("30.00");
		motService.setSelectedVehicle(selectedVehicle);
		motJSON[0] = motService;
		return motJSON;
	}
	
	private OSBOVService[] createValueJSON() {
		OSBOVService [] valueJSON = new OSBOVService [1];
		OSBOVService valueService =  new OSBOVService();
		valueService.setUniqueId(VALUE_UNIQUE_ID);
		valueService.setDealerCode("46396AA");
		valueService.setLocale("en-gb");
		valueService.setMarketCode("GBR");
		valueService.setModel("Focus");
		valueService.setServiceType(OldServiceType.VALUE);
		SelectedVehicleWithPrice selectedVehicle = new SelectedVehicleWithPrice();
		selectedVehicle.setPrice("115.00");
		selectedVehicle.setDescription("Major Service - includes 30-point FORD eCHECK, oil & filter change, fluid top-ups, Ford Roadside Assistance");
		selectedVehicle.setPriceAfterDiscount("110.00");
		valueService.setName("Major Service");
		valueService.setSelectedVehicle(selectedVehicle);
		valueJSON[0] = valueService;
		return valueJSON;
	}
	
	private OSBOVService[] createRepairJSON() {
		OSBOVService [] repairJSON = new OSBOVService [1];
		OSBOVService repairService =  new OSBOVService();
		repairService.setUniqueId(REPAIR_UNIQUE_ID);
		repairService.setName("Repair with Service");
		repairService.setDealerCode("46396AA");
		repairService.setLocale("en-gb");
		repairService.setMarketCode("GBR");
		repairService.setModel("Focus");
		repairService.setServiceType(OldServiceType.REPAIR);
		SelectedVehicleWithPrice selectedVehicle = new SelectedVehicleWithPrice();
		selectedVehicle.setPrice("35.00");
		selectedVehicle.setDescription("If booked together with a Motorcraft Minor or Major service, enjoy an extra \u00c2\u00a35 off Plus earn triple Nectar Points on the MOT (6 points per \u00c2\u00a31 spent)");
		selectedVehicle.setPriceAfterDiscount("30.00");
		repairService.setSelectedVehicle(selectedVehicle);
		repairJSON[0] = repairService;
		return repairJSON;
	}
}