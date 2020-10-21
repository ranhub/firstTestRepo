package com.ford.turbo.servicebooking.models.osb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSBCBookingCreateBookingPostString {
	private String marketCode;
	private OSBCStandardService standardService;
	private String customerAnnotation;
	private String dealerCode;
	private String vin;
	private String applicationInformation;
	private String registrationNumber;
	private List<OSBCBookedAdditionalService> bookedAdditionalServices;
	private OSBCDealerSearchLoadDealersDetailsData dealer;
	private String mainServicePrice;
	private OSBCCustomer customer;
	private String serviceType;
	private String odometer;
	private String vehicleLineCode;
	private String locale;
	private OSBCTimeAsDate appointmentTimeAsDate;
	private String mainServiceId;
	private OSBCBookingCreateBookingPostStringFsaActions fsaActions;
	private VehicleDetails vehicleDetails;
	@JsonProperty("motJSON")
	private List<OSBOVService> mots;
	@JsonProperty("valueServiceJSON")
	private List<OSBOVService> values;
	@JsonProperty("repairsJSON")
	private List<OSBOVService> repairs;
	private String voucherCodes;
	@JsonProperty("OSBSiteTermsRequired")
	private boolean osbSiteTermsRequired;
}
