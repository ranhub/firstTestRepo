package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.utils.Utilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class EUOSBDealerProfileResponse {
	private String street;
	private String dealerName;
	private String dealerCode;
	private String district;
	private String phone;
	private final Map<String, String> openingHours = Utilities.getWeekDayHashMap();
	private String town;
	private String country;
	private String postalCode;
	private String email;
}
