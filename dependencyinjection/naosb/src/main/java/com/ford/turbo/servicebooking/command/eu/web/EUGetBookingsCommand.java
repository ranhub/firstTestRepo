package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.GetBookingsRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBBookedServicesResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUGetBookingsCommand extends TimedHystrixCommand<EUOSBBookedServicesResponse> {
	private static final String GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String EU_OSB_GET_BOOKING_PATH = "/rest/v1/booking?email=%s&accessCode=%s";

	private String baseUrl;
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private GetBookingsRequest request;

	public EUGetBookingsCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                @NotNull String baseUrl, @NotNull GetBookingsRequest request) {

		super(traceInfo, GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = baseUrl;
		this.request = request;
	}

	@Override
	public EUOSBBookedServicesResponse doRun() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		
		String url = constructBaseUrl();
		
		ResponseEntity<EUOSBBookedServicesResponse> responseEntity = mutualAuthRestTemplate.exchange(url,
				HttpMethod.GET, requestEntity, EUOSBBookedServicesResponse.class);

		EUOSBBookedServicesResponse response = responseEntity.getBody();

		log.debug("Response body: {}", response);

		return response;
	}

	protected String constructBaseUrl() {
		return baseUrl + String.format(EU_OSB_GET_BOOKING_PATH, request.getEmail(), request.getAccessCode());
	}
}
