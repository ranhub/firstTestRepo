package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EUOSBGetBookingsData {
	private String appointmentTime;
	private EUOSBDealerProfileResponse dealerProfile;
	private boolean previousBooking;
	private String bookingReferenceNumber;
	private String comments;
	private List<EUOSBOldServiceResponse> oldServices;
	private List<EUOSBAdditionalServiceResponse> additionalServices;
	private EUOSBVehicleDetailsResponse vehicleDetails;
	private EUOSBMainServiceResponse mainService;
	private List<EUOSBServiceVoucher> voucherCodes;
	private String vehicleLineDescription;
	private EUOSBCustomerResponse customer;
}
