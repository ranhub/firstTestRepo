package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleLookupRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleLookupResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
 public class EUWebVehicleLookupCommand extends TimedHystrixCommand<EUOSBVehicleLookupResponse> {
    private static final String EU_OSB_VEHICLE_DETAILS_URL = "/rest/v1/vehicle/vehicleDetails";
    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private final String baseUrl;
    private final EUWebVehicleLookupRequest vehicleLookupRequest;

	public EUWebVehicleLookupCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                     @NotNull String baseUrl, @NotNull EUWebVehicleLookupRequest vehicleLookupRequest) {
		super(traceInfo, "PUBLIC_EU_GROUP");
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = baseUrl;
		this.vehicleLookupRequest = vehicleLookupRequest;
	}
 
	@Override
	public EUOSBVehicleLookupResponse doRun() throws Exception {
        String url = baseUrl + EU_OSB_VEHICLE_DETAILS_URL;
		
        HttpHeaders headers = new HttpHeaders();
        Utilities.populateRequestTraceForCommand(headers, this);
        HttpEntity<EUWebVehicleLookupRequest> requestEntity = new HttpEntity<EUWebVehicleLookupRequest>(vehicleLookupRequest, headers);
		log.info("Request body: {}", vehicleLookupRequest);
		 
		ResponseEntity<EUOSBVehicleLookupResponse> responseEntity = mutualAuthRestTemplate.postForEntity(url, requestEntity, EUOSBVehicleLookupResponse.class);
		EUOSBVehicleLookupResponse euOSBVehicleLookupResponse = responseEntity.getBody();
        
		log.debug("Response body: {}", euOSBVehicleLookupResponse);
        
		return euOSBVehicleLookupResponse;
	}
}
