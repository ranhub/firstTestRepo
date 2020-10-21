package com.ford.turbo.servicebooking.service.eu.web;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.EUCreateBookingCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;

@RunWith(MockitoJUnitRunner.class)
public class EUWebBookingServiceCreateBookingTest {
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Mock
	private CredentialsSource mockEUOSBCredentialsSource;
	@Mock
	private EUCreateBookingCommand mockCommand;
	@Spy
	@InjectMocks
	private EUWebBookingService service;
	
	@Test
	public void shouldReturnResponse_whenCreateBooking() throws Exception {
		
		CreateBookingWebRequest webRequest = CreateBookingWebRequest.builder().build();
		CreateBookingOSBRequest osbRequest = CreateBookingOSBRequest.builder().build();
		doReturn(osbRequest).when(service).mapWebRequestToOSBRequest(webRequest);
		doReturn(mockCommand).when(service).getEUCreateBookingCommand(osbRequest);
		EUOSBCreateBookingResponse osbResponse = EUOSBCreateBookingResponse.builder().build();
		CreateBookingWebWrapper osbData = CreateBookingWebWrapper.builder()
				.accessCode("my-access-code")
				.bookingReferenceNumber("my-booking-ref")
				.build();
		osbResponse.setData(osbData);
		when(mockCommand.execute()).thenReturn(osbResponse);
		CreateBookingWebWrapper wrapper = service.createBooking(webRequest);
		verify(mockCommand).execute();
		verify(service).getEUCreateBookingCommand(osbRequest);
		assertThat(wrapper.getAccessCode()).isEqualTo("my-access-code");
		assertThat(wrapper.getBookingReferenceNumber()).isEqualTo("my-booking-ref");
	}
	
	@Test
	public void shouldReturnCommand() {
		
		EUWebBookingService service = new EUWebBookingService(mockTraceInfo, mockMutualAuthRestTemplate, mockEUOSBCredentialsSource);
		CreateBookingOSBRequest request = CreateBookingOSBRequest.builder().build();
		EUCreateBookingCommand command = service.getEUCreateBookingCommand(request);
		assertThat(TimedHystrixCommand.class.isAssignableFrom(command.getClass()));
	}
}
