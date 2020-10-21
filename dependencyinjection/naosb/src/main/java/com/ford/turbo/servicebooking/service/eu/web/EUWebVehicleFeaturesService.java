package com.ford.turbo.servicebooking.service.eu.web;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleFeaturesData;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.WebServiceVehicleFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.eu.web.EUWebVehicleFeaturesCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleFeaturesResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EUWebVehicleFeaturesService implements WebServiceVehicleFeatureService {

	private TraceInfo traceInfo;
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String baseUrl;

	@Autowired
	public EUWebVehicleFeaturesService(TraceInfo traceInfo, MutualAuthRestTemplate mutualAuthRestTemplate,
			@Qualifier("OSB_DATAPOWER") CredentialsSource euOsbCredentialsSource) {
		this.traceInfo = traceInfo;
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = euOsbCredentialsSource.getBaseUri();
	}

	public EUWebVehicleFeaturesData getVehicleFeatures(EUWebVehicleFeaturesRequest request) {
		log.info("Get Vehicle Features for locale: " + request.getLocale() + " and Market Code: "
				+ request.getMarketCode());

		EUOSBVehicleFeaturesResponse response = getEUWebVehicleFeaturesCommand(request).execute();
		if (response.getError() != null) {
			FordError fordError = new FordError("EU OSB", (Integer) response.getError().get("statusCode"),
					(String) response.getError().get("code"));
			throw new BadRequestException(fordError);
		}

		EUWebVehicleFeaturesData vehicleFeaturesData = response.getData();

		return vehicleFeaturesData;
	}

	protected EUWebVehicleFeaturesCommand getEUWebVehicleFeaturesCommand(EUWebVehicleFeaturesRequest request) {
		return new EUWebVehicleFeaturesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}
}
