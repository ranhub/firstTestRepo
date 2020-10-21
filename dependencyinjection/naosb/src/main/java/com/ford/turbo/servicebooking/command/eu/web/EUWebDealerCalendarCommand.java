package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerCalendarRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerCalendarResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUWebDealerCalendarCommand extends TimedHystrixCommand<EUOSBDealerCalendarResponse> {

	private static final String GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String EU_OSB_DEALER_CALENDAR_MANDATORY_PARAMS_PATH = "/rest/v1/dealer/calendar?marketCode=%s&locale=%s&dealerCode=%s";

	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String url;
	
	public EUWebDealerCalendarCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                      @NotNull String baseUrl, @NotNull DealerCalendarRequest request) {
		
		super(traceInfo, GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.url = constructOsbDealerCalendarUrl(baseUrl, request);
	}

	@Override
	public EUOSBDealerCalendarResponse doRun() throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		ResponseEntity<EUOSBDealerCalendarResponse> responseEntity = mutualAuthRestTemplate.exchange(url,
				HttpMethod.GET, requestEntity, EUOSBDealerCalendarResponse.class);
		log.debug("Response body: {}", responseEntity.getBody());
		
		return responseEntity.getBody();
	}
	
	protected String constructOsbDealerCalendarUrl(String baseUrl, DealerCalendarRequest request) {
		
		String url = baseUrl + String.format(EU_OSB_DEALER_CALENDAR_MANDATORY_PARAMS_PATH, request.getMarketCode(), request.getLocale(), request.getDealerCode());
		
		if (StringUtils.isNotBlank(request.getModelName())) {
			url += "&modelName=" + request.getModelName();
		}
		if (request.getAdditionalService() != null && !request.getAdditionalService().isEmpty()) {
			String commaSeparatedAdditionalServices = null;
			for (String service : request.getAdditionalService()) {
				if (commaSeparatedAdditionalServices == null) {
					commaSeparatedAdditionalServices = "&additionalServices=" + service;
				} else {
					commaSeparatedAdditionalServices += "," + service;
				}
			}
			url += commaSeparatedAdditionalServices;
		}
		if (StringUtils.isNotBlank(request.getMotService())) {
			url += "&motServiceID=" + request.getMotService();
		}
		return url;
	}
}
