package com.ford.turbo.servicebooking.command.eu.web;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUAccessCodesNotificationCommand extends TimedHystrixCommand<EUOSBAccessCodesNotificationResponse> {

	private static final String ACCESS_CODE_NOTIFICATION_COMMAND_GROUP_KEY = "PUBLIC_EU_GROUP";
	private final String accessCodeNotificationUrl = "/rest/v1/booking/forgottenAccessCode?marketCode=%s&email=%s&osbSiteTermsRequired=%s";

	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String url;

	public EUAccessCodesNotificationCommand(TraceInfo traceInfo,
                                            @NotNull MutualAuthRestTemplate mutualAuthRestTemplate, @NotNull String baseUrl,
                                            @NotNull AccessCodesNotificationRequest request) {

		super(traceInfo, ACCESS_CODE_NOTIFICATION_COMMAND_GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.url = baseUrl + String.format(accessCodeNotificationUrl, request.getMarketCode(), request.getEmail(),
				request.getOsbSiteTermsRequired());
	}

	@Override
	public EUOSBAccessCodesNotificationResponse doRun() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
		
		ResponseEntity<EUOSBAccessCodesNotificationResponse> responseEntity = mutualAuthRestTemplate.exchange(url,
				HttpMethod.GET, requestEntity, EUOSBAccessCodesNotificationResponse.class);
		
		log.debug("Response body: {}", responseEntity.getBody());
		
		return responseEntity.getBody();
	}
}
