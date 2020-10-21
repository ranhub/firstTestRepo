package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleFeaturesData;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EUOSBVehicleFeaturesResponse extends OSBBaseResponse<EUWebVehicleFeaturesData> {
	
	private Map<String, Object> error;

	public Map<String, Object> getError() {
		return error;
	}

	public void setError(Map<String, Object> error) {
		this.error = error;
	}
	
}