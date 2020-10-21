package com.ford.turbo.servicebooking.command;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.util.ServiceAuthenticationWrapper;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.response.VinLookupDetailsResponse;
import com.ford.turbo.servicebooking.models.msl.response.VinLookupResponse;
import com.ford.turbo.servicebooking.utils.Utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VinLookupCommand extends TimedHystrixCommand<VinLookupDetailsResponse> {

    private final String vin;
    private final ServiceAuthenticationWrapper serviceAuthenticationWrapper;
    private final RestTemplate restTemplate;
    private final String vinlookupServiceHost;

    public VinLookupCommand(
            @NotNull TraceInfo traceInfo,
            RestTemplate restTemplate,
            String vin,
            ServiceAuthenticationWrapper serviceAuthenticationWrapper,
            String vinLookupBaseUrl) {
        super(traceInfo, "VinLookupCommand");
        this.vin = vin;
        this.serviceAuthenticationWrapper = serviceAuthenticationWrapper;
        this.restTemplate = restTemplate;
        this.vinlookupServiceHost = vinLookupBaseUrl;
    }

    @Override
    public VinLookupDetailsResponse doRun() throws Exception {
    	String url = vinlookupServiceHost + "/v1/vins/" + vin + "/detail";
        HttpHeaders headers = new HttpHeaders();

        Utilities.populateRequestTraceForCommand(headers, this);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("application-id", serviceAuthenticationWrapper.getApplicationId());
        populateAuthHeader(headers);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<VinLookupResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, VinLookupResponse.class);

        VinLookupResponse body = responseEntity.getBody();
        log.debug("Response body: {}", body);
        
        return body.getValue();
    }

	protected void populateAuthHeader(HttpHeaders headers) {
		if (ServiceAuthenticationWrapper.AuthenticationMethods.FIG.equals(serviceAuthenticationWrapper.getAuthenticationMethod())) {
            headers.set("auth-token", serviceAuthenticationWrapper.getFigAuthorizationToken());
        } else {
            headers.set("Authorization", "Bearer "+serviceAuthenticationWrapper.getOauthBearerToken());
        }
	}
}
