package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.EUDealersRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealersResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUDealersCommand extends TimedHystrixCommand<EUOSBDealersResponse> {
	
	private static final String DEALER_COMMAND_GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String EU_OSB_DEALER_SEARCH_URL = "/rest/v1/dealer/dealerData";
	
	private MutualAuthRestTemplate mutualAuthRestTemplate;
    private String url;

	public EUDealersCommand(TraceInfo traceInfo,
                            @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                            @NotNull String baseUrl,
                            @NotNull EUDealersRequest request) {
		super(traceInfo, DEALER_COMMAND_GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.url = baseUrl + EU_OSB_DEALER_SEARCH_URL + "?marketCode=" + request.getMarketCode();
	}

	@Override
	public EUOSBDealersResponse doRun() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		
		ResponseEntity<EUOSBDealersResponse> responseEntity = mutualAuthRestTemplate.exchange(url, HttpMethod.GET, requestEntity, EUOSBDealersResponse.class);
		
		EUOSBDealersResponse dealersResponse = responseEntity.getBody();
		log.debug("Response body: {}", dealersResponse);
		
		return responseEntity.getBody();
	}
}
