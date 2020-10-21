package com.ford.turbo.servicebooking.models.eu.web;

import java.util.Map;

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
public class DealerProfile {
	private String street;
	private String dealerName;
	private String dealerCode;
	private String district;
	private String phone;
	private Map<String, String> openingHours;
	private String town;
	private String country;
	private String postalCode;
	private String email;
}
