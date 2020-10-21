package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.CancelBookingRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUCancelBookingCommand extends TimedHystrixCommand<EUOSBCancelBookingResponse> {
	private static final String DEALER_DETAILS_COMMAND_GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String EU_OSB_CANCEL_BOOKING_PATH = "/rest/v1/booking?bookingReferenceNumber=%s&accessCode=%s&osbSiteTermsRequired=%s";
	
	private String url;
	private MutualAuthRestTemplate mutualAuthRestTemplate;

	public EUCancelBookingCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate, @NotNull String baseUrl,
                                  @NotNull CancelBookingRequest request) {
		
		super(traceInfo, DEALER_DETAILS_COMMAND_GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.url = baseUrl + String.format(EU_OSB_CANCEL_BOOKING_PATH, request.getBookingReferenceNumber(), request.getAccessCode(), request.isOsbSiteTermsRequired());
	}

	@Override
	public EUOSBCancelBookingResponse doRun() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		
		ResponseEntity<EUOSBCancelBookingResponse> responseEntity = mutualAuthRestTemplate.exchange(url, HttpMethod.DELETE,
				requestEntity, EUOSBCancelBookingResponse.class);
		EUOSBCancelBookingResponse response = responseEntity.getBody();
		
		log.debug("Response body: {}", response);
		
		return response;
	}
}
