package com.ford.turbo.servicebooking.command.eu;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUCreateBookingCommand extends TimedHystrixCommand<EUOSBCreateBookingResponse> {
	
	private static final String OSB_CREATE_BOOKING_URL = "/rest/v1/booking";
	private static final String COMMAND_GROUP_KEY = "PUBLIC_EU_GROUP";
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private CreateBookingOSBRequest request;
	private String url;

	public EUCreateBookingCommand(TraceInfo traceInfo,
                                  MutualAuthRestTemplate mutualAuthRestTemplate, String baseUrl, CreateBookingOSBRequest request) {

		super(traceInfo, COMMAND_GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.request = request;
		this.url = baseUrl + OSB_CREATE_BOOKING_URL;
	}

	@Override
	public EUOSBCreateBookingResponse doRun() throws Exception {
		
		logRequest();
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<CreateBookingOSBRequest> httpEntity = new HttpEntity<CreateBookingOSBRequest>(request, headers);
		ResponseEntity<EUOSBCreateBookingResponse> response = mutualAuthRestTemplate.exchange(url, HttpMethod.POST,
				httpEntity, EUOSBCreateBookingResponse.class);
		if(response.getBody().getData() != null) {
			log.info("created booking reference number: " + response.getBody().getData().getBookingReferenceNumber());
		}
		return response.getBody();
	}

	private void logRequest() {
		if (request.getVin() != null) {
			log.info("create booking by vin: " + request.getVin());
		}
		if (request.getRegistrationNumber() != null) {
			log.info("create booking by registration number: " + request.getRegistrationNumber());
		}
		if (request.getModelName() != null && request.getBuildYear() != null) {
			log.info("create booking by model name and build year: " + request.getModelName() + " " + request.getBuildYear());
		}
	}

}

