package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleFeaturesResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUWebVehicleFeaturesCommand extends TimedHystrixCommand<EUOSBVehicleFeaturesResponse> {

	private static final String EU_OSB_VEHICLE_FEATURES_URL_PATH = "/rest/v1/vehicle/vehicleData";
	private final MutualAuthRestTemplate mutualAuthRestTemplate;
	private final String baseUrl;
	private final EUWebVehicleFeaturesRequest request;

	public EUWebVehicleFeaturesCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                       @NotNull String baseUrl, @NotNull EUWebVehicleFeaturesRequest request) {
		super(traceInfo, "PUBLIC_EU_GROUP");
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = baseUrl;
		this.request = request;
	}

	@Override
	public EUOSBVehicleFeaturesResponse doRun() throws Exception {
		String url = baseUrl + EU_OSB_VEHICLE_FEATURES_URL_PATH + "?l=" + request.getLocale() + "&mc="
				+ request.getMarketCode();
		
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);

		ResponseEntity<EUOSBVehicleFeaturesResponse> responseEntity = mutualAuthRestTemplate.exchange(url,
				HttpMethod.GET, requestEntity, EUOSBVehicleFeaturesResponse.class);

		EUOSBVehicleFeaturesResponse body = responseEntity.getBody();
		log.debug("Response body: {}", body);

		return body;
	}
}