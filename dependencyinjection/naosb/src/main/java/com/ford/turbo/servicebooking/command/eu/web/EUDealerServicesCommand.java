
package com.ford.turbo.servicebooking.command.eu.web;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;

public class EUDealerServicesCommand extends TimedHystrixCommand<EUOSBDealerServicesWebResponse> {

	private static final String GROUP_KEY = "PUBLIC_EU_GROUP";
	private static final String SERVICES_PATH = "/rest/v1/dealer/services";
	private static final String QUERY_PARAMS_PATTERN = "?dealerCode=%s&marketCode=%s&locale=%s";

	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String baseUrl;
	private DealerServicesRequest request;
	
	
	public EUDealerServicesCommand(TraceInfo traceInfo,
                                   MutualAuthRestTemplate mutualAuthRestTemplate,
                                   String baseUrl,
                                   DealerServicesRequest request) {
		super(traceInfo, GROUP_KEY);
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = baseUrl;
		this.request = request;
	}

	@Override
	public EUOSBDealerServicesWebResponse doRun() throws Exception {
		String url = getRequestUrl();

		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);

		ResponseEntity<EUOSBDealerServicesWebResponse> responseEntity = mutualAuthRestTemplate.exchange(url,
				HttpMethod.GET, requestEntity, EUOSBDealerServicesWebResponse.class);

		return responseEntity.getBody();
	}

	protected String getRequestUrl() {
		StringBuffer urlBuffer = new StringBuffer(baseUrl);

		urlBuffer.append(SERVICES_PATH);

		urlBuffer.append(String.format(QUERY_PARAMS_PATTERN, 
									changeEmptyIfNull(request.getDealerCode()), 
									changeEmptyIfNull(request.getMarketCode()),
									changeEmptyIfNull(request.getLocale())));
		
		urlBuffer.append(constructRequestParams());

		return urlBuffer.toString();
	}
	
	protected String constructRequestParams() {
		StringBuffer requestParams = new StringBuffer("");

		if (request.getBuildYear() != null) {
			requestParams.append("&buildYear=");
			requestParams.append(request.getBuildYear());
		}
		if (request.getModelName() != null) {
			requestParams.append("&modelName=");
			requestParams.append(request.getModelName());
		}
		if (request.getVin() != null) {
			requestParams.append("&vin=");
			requestParams.append(request.getVin());
		}
		if (request.getRegistrationNumber() != null) {
			requestParams.append("&registrationNumber=");
			requestParams.append(request.getRegistrationNumber());
		}
		if (request.getMileage() != null) {
			requestParams.append("&mileage=");
			requestParams.append(request.getMileage());
		}
		
		if (request.getCombinedVoucherCodes() != null) {
			requestParams.append("&voucherCodes=");
			requestParams.append(request.getCombinedVoucherCodes());
		}

		return requestParams.toString();
	}
	
	protected static String changeEmptyIfNull(String arg){
		return arg == null ? "" : arg;
	}
}