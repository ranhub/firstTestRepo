package com.ford.turbo.servicebooking.service;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleFeaturesData;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;


public interface WebServiceVehicleFeatureService {
	default public EUWebVehicleFeaturesData getVehicleFeatures(EUWebVehicleFeaturesRequest request) {
		throw new NoBackendAvailableException();
	}
}