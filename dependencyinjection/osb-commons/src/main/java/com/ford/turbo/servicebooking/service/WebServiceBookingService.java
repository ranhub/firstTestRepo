package com.ford.turbo.servicebooking.service;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWrapper;


public interface WebServiceBookingService {
	default public VehicleDetailsWrapper getVehicleLookup(String vin, String registrationNumber, String locale, String marketCode,
			long mileage, String ecatMarketCode, boolean osbSiteTermsRequired) throws Exception {
		throw new NoBackendAvailableException();
	}
}