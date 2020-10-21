package com.ford.turbo.servicebooking.models.msl.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUWebVehicleLookupRequest {
	
	private String vin;
	private String registrationNumber;
	private String locale;
	private String marketCode;
	private long mileage;
	private String ecatMarketCode;
	private boolean osbSiteTermsRequired;

}
