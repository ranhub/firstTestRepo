
package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerDetailsResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUDealerDetailsCommand extends TimedHystrixCommand<EUOSBDealerDetailsResponse> {

	private static final String DEALER_DETAILS_COMMAND_GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String EU_OSB_DEALER_INFO_PATH = "/rest/v1/dealer/dealerInfo";

	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String url;

	public EUDealerDetailsCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                  @NotNull String baseUrl, @NotNull DealersDetailsRequest request) {
		
		super(traceInfo, DEALER_DETAILS_COMMAND_GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.url = constructRequestUrl(baseUrl, request);
	}

	@Override
	public EUOSBDealerDetailsResponse doRun() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		
		ResponseEntity<EUOSBDealerDetailsResponse> responseEntity = mutualAuthRestTemplate.exchange(url, HttpMethod.GET,
				requestEntity, EUOSBDealerDetailsResponse.class);
		
		EUOSBDealerDetailsResponse dealersResponse = responseEntity.getBody();
		log.debug("Response body: {}", dealersResponse);
		
		return responseEntity.getBody();
	}

	protected String constructRequestUrl(String baseUrl, DealersDetailsRequest request) {
		String requestUrl = baseUrl + EU_OSB_DEALER_INFO_PATH + "?marketCode=" + request.getMarketCode() + "&locale="
				+ request.getLocale();
		String commaSeparatedDealerCodes = null;
		for (String dealerCode : request.getDealerCodes()) {
			if (commaSeparatedDealerCodes == null) {
				commaSeparatedDealerCodes = dealerCode;
			} else {
				commaSeparatedDealerCodes += "," + dealerCode;
			}
		}
		if(StringUtils.isNotBlank(commaSeparatedDealerCodes)) {
			requestUrl += "&dealerCodes=" + commaSeparatedDealerCodes;
		}
		if (StringUtils.isNotBlank(request.getVin())) {
			requestUrl += "&vin=" + request.getVin();
		}
		if (StringUtils.isNotBlank(request.getRegistrationNumber())) {
			requestUrl += "&registrationNumber=" + request.getRegistrationNumber();
		}
		if (StringUtils.isNotBlank(request.getModelName())) {
			requestUrl += "&modelName=" + request.getModelName();
		}
		if (StringUtils.isNotBlank(request.getBuildYear())) {
			requestUrl += "&buildYear=" + request.getBuildYear();
		}
		return requestUrl;
	}
}