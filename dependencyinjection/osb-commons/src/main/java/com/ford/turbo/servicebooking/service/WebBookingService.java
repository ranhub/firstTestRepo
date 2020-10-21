package com.ford.turbo.servicebooking.service;

import org.springframework.stereotype.Component;

import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.response.AccessCodesNotificationWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.CancelBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.GetBookingsWebWrapper;

@Component
public interface WebBookingService {

	public CancelBookingWebWrapper cancelBooking(String bookingRefNumber, String accessCode, boolean osbSiteTermsRequired);

	public AccessCodesNotificationWebWrapper sendAccessCodesNotification(AccessCodesNotificationRequest request);
	
	public GetBookingsWebWrapper getBookings(String accessCode, String email);
	
	public CreateBookingWebWrapper createBooking(CreateBookingWebRequest request);
}
