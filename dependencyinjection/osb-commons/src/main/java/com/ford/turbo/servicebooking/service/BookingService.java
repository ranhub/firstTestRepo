package com.ford.turbo.servicebooking.service;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;
import com.ford.turbo.aposb.common.authsupport.util.ServiceAuthenticationWrapper;
import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.request.UpdateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.msl.response.v2.BookedServiceV2Response;
import com.ford.turbo.servicebooking.models.msl.response.v2.BookingDetailsServiceResponseValue;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.CancelBookedServiceResponse;

@Component
public interface BookingService {
    public BookedServiceResponse listServiceBookings(String opusConsumerId, String marketCode, Optional<String> vin);

	public CancelBookedServiceResponse cancelBooking(String bookingRefNumber, String marketCode, String appId, String authToken);

	public String createBooking(CreateBookingRequest requestBody, String appId, String authToken) throws Exception;

	default public String updateBooking(UpdateBookingRequest requestBody, UserIdentity userIdentity,
			ServiceAuthenticationWrapper serviceAuthenticationWrapper) throws Exception {
		return null;
	}
	
	public BookingDetailsServiceResponseValue getBookingDetails(String bookingId,String appId);

	default public BookedServiceV2Response getUserBookings(String appId, String authToken, UserIdentity userIdentity) throws Exception {
		throw new NoBackendAvailableException(); 
	}
}
