package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EUOSBBookedServicesResponse {
	private EUOSBGetBookingsData data;
	private EUOSBWebError error;	
}
