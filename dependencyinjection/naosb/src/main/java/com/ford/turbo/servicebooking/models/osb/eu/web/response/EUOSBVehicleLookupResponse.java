package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUOSBVehicleLookupResponse {
	private EUWebVehicleDetails vehicleDetails;
	private EUOSBWebError error;
}
