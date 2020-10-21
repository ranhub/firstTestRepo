package com.ford.turbo.aposb.common.authsupport.userprofile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetUserProfileCommand extends TimedHystrixCommand<UserProfile> {
    private final TraceInfo traceInfo;
    private final RestTemplate restTemplate;
    private final String appId;
    private final String authToken;
    private final ContinentCodeExtractor continentCodeExtractor;
    private static final String MASK = "**********";

    private static final String HYSTRIX_GROUP_KEY = "GetUserProfileCommand";

    public GetUserProfileCommand(ContinentCodeExtractor continentCodeExtractor, TraceInfo traceInfo, RestTemplate restTemplate, String appId, String authToken) {
        super(traceInfo, HYSTRIX_GROUP_KEY);
        this.traceInfo = traceInfo;
        this.restTemplate = restTemplate;
        this.appId = appId;
        this.authToken = authToken;
        this.continentCodeExtractor = continentCodeExtractor;
    }

    @Override
    public UserProfile doRun() throws Exception {

        HttpHeaders requestHeaders = getRequestHeaders();

        CredentialsSource credentialsSource = getCredentialsSource();

        String url = credentialsSource.getBaseUri() + "/users";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("lrdt", LocalDate.now().toString());

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);
        logRequest(requestHeaders, url.toString());
        
        ResponseEntity<UserProfileResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, UserProfileResponse.class);
        
        logResponse(response.getStatusCode(),response.getHeaders(),response.getBody());
        
        return response.getBody().getProfile();
    }

    private CredentialsSource getCredentialsSource() throws IOException {
        ContinentCode continentCode = continentCodeExtractor.getContinent(appId);
        CredentialsSource credentialsSource = null;
        switch (continentCode) {
            case AP:
            case NA:
                credentialsSource = new CredentialsSource("NGSDN");
                break;
            case EU:
                credentialsSource = new CredentialsSource("EUCRM");
                break;
        }
        return credentialsSource;
    }
    
    private void logRequest(HttpHeaders requestHeaders, String url) {
        log.info("Request URL: " + url);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Request headers: ");
        
        String headersString = requestHeaders.keySet()
	        .stream()
	        .map(header -> {
				if (header.equalsIgnoreCase("auth-token")) {
					return header + "=" + requestHeaders.get(header).get(0).substring(0, 16) + MASK;
				} return header + "=" + requestHeaders.get(header);
			})
	        .collect(Collectors.joining(","));
        stringBuilder.append(headersString);
        log.info(stringBuilder.toString());
    }
    
    private void logResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, UserProfileResponse userProfileResponse) {
        log.info("Response Status Code: " + httpStatus);
        log.info("Response Headers: " + httpHeaders);
        
        if(userProfileResponse.getError() != null) {
        	log.info("Response Body: " + userProfileResponse);
        } else if(log.isDebugEnabled()) {
        	log.debug("Response Body: " + userProfileResponse);
        }
    }
    
    public HttpHeaders getRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("auth-token", authToken);
        if (appId != null) {
            requestHeaders.add("Application-Id", appId);
        }
        requestHeaders.add("X-B3-TraceId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getTraceId()));
        requestHeaders.add("X-B3-SpanId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getSpanId()));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }
}
