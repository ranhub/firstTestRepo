package com.ford.turbo.servicebooking.models.msl.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EUWebVehicleFeaturesRequest {
	
	private String locale;
	private String marketCode;
	
}
