package com.ford.turbo.servicebooking.models.msl.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.CustomerWeb;
import com.ford.turbo.servicebooking.models.eu.web.DealerProfile;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWeb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBookingsData {
	private String appointmentTime;
	private DealerProfile dealer;
	private boolean previousBooking;
	private String bookingReferenceNumber;
	private String comments;
	private List<OldServicesWeb> oldServices;
	private List<AdditionalServicesWeb> additionalServices;
	private VehicleDetailsWeb vehicleDetails;
	private MainServicesWeb mainService;
	private List<ServiceVoucher> voucherCodes;
	private String vehicleLineDescription;
	private CustomerWeb customer;

}
