package com.ford.turbo.servicebooking.models.msl.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealersDetailsRequest {

	private String marketCode; 
	private String locale;
	private List<String> dealerCodes;
	private String modelName;
	private String buildYear;
	private String registrationNumber;
	private String vin;
}
