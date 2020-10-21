package com.ford.turbo.servicebooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.exception.InvalidBookingNumberException;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.CancelBookedServiceResponse;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.CancelBookingCommand;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUBookingServiceTest {

	private EUBookingService service;
	private EUBookingService spyService;
	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;

	@Mock
	private CredentialsSource credentialsSource;

	@Mock
	private UserProfileService userProfileService;

	private String marketCode = "MAARKE_CODE";

	private String appId = "APP_ID";

	private String authToken = "AUTH_TOKEN";

	private String bookingRefNumber1 = "BOOKING_REF_NUMBER_1";
	private String bookingRefNumber2 = "BOOKING_REF_NUMBER_2";
	private String bookingRefNumber3 = "BOOKING_REF_NUMBER_3";
	private String bookikngRefInvalid = "BOOKING_REF_INVALID";
	
	
	private String opusConsumerId = "CONSUMER_ID";

	@Captor
	private ArgumentCaptor<String> bookingRefCaptor;
	
	@Captor
	private ArgumentCaptor<String> customerIdCaptor;
	
	@Captor
	private ArgumentCaptor<String> marketCodeCaptor;

	@Before
	public void setup() {
		service = new EUBookingService(mutualAuthRestTemplate, traceInfo, credentialsSource, userProfileService);
		spyService = Mockito.spy(service);
	}

	@Test
	public void shouldCancelBooking_when_referenceNumber_IsPresent() {

		mockUserProfile();
		
		mockListServiceBookingsForDelete();
		
		CancelBookingCommand mockCancelBookingCommand = mockCancelBookingCommand(true);
		
		CancelBookedServiceResponse cancelBooking = spyService.cancelBooking(bookingRefNumber2, marketCode, appId, authToken);

		assertThat(cancelBooking).isNotNull();
		assertThat(cancelBooking.isCancelled()).isEqualTo(true);
		
		verify(mockCancelBookingCommand).execute();
		verify(spyService).listServiceBookingsForDelete(customerIdCaptor.capture(), marketCodeCaptor.capture());
		
		assertThat(bookingRefCaptor.getValue()).isEqualTo(bookingRefNumber2);
		assertThat(customerIdCaptor.getValue()).isEqualTo(opusConsumerId);
		assertThat(marketCodeCaptor.getValue()).isEqualTo(marketCode);
	}
	
	@Test(expected= InvalidBookingNumberException.class)
	public void shouldCancelBooking_return_False_when_PassedInvalidReferenceNumber() {
		mockUserProfile();
		
		mockListServiceBookingsForDelete();
		
		spyService.cancelBooking(bookikngRefInvalid, marketCode, appId, authToken);
		
		verify(spyService).listServiceBookingsForDelete(customerIdCaptor.capture(), marketCodeCaptor.capture());
		
		assertThat(customerIdCaptor.getValue()).isEqualTo(opusConsumerId);
		assertThat(marketCodeCaptor.getValue()).isEqualTo(marketCode);
	}

	private void mockUserProfile() {
		UserProfile userProfile = UserProfile.builder().userId(opusConsumerId).build();
		when(userProfileService.getUserProfile(authToken, appId)).thenReturn(userProfile);
	}

	private CancelBookingCommand mockCancelBookingCommand(boolean cancelled) {
		CancelBookingCommand mockCancelBookingCommand = mock(CancelBookingCommand.class);
		doReturn(mockCancelBookingCommand).when(spyService).getCancelBookingCommand(bookingRefCaptor.capture());
		
		OSBCCancelBookingResponse osbcCancelBookingResponse = new OSBCCancelBookingResponse();
		OSBCCancelBookingData data = OSBCCancelBookingData.builder().isBookingCancelled(cancelled).build();
		osbcCancelBookingResponse.setData(data);
		when(mockCancelBookingCommand.execute()).thenReturn(osbcCancelBookingResponse);
		return mockCancelBookingCommand;
	}

	private void mockListServiceBookingsForDelete() {
		List<BookedServiceResponse> listBookingServices = new ArrayList<BookedServiceResponse>();
		BookedServiceResponse bookingService1 = BookedServiceResponse.builder().bookingCustomerRefNum(bookingRefNumber1).build();
		BookedServiceResponse bookingService2 = BookedServiceResponse.builder().bookingCustomerRefNum(bookingRefNumber2).build();
		BookedServiceResponse bookingService3 = BookedServiceResponse.builder().bookingCustomerRefNum(bookingRefNumber3).build();
		listBookingServices.add(bookingService1);
		listBookingServices.add(bookingService2);
		listBookingServices.add(bookingService3);
		doReturn(listBookingServices).when(spyService).listServiceBookingsForDelete(customerIdCaptor.capture(), marketCodeCaptor.capture());
	}
	
}

