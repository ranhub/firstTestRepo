package com.ford.turbo.aposb.common.authsupport.userprofile;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetUserProfileFromCSDNCommand extends TimedHystrixCommand<CommonSDNUserProfileResponse> {
	
	private static final String HYSTRIX_GROUP_KEY = "GetUserProfileFromCSDNCommand";
	private final TraceInfo traceInfo;
	private String token;
	private String applicaitonId;
	private String guid;
	private CredentialsSource commonSDN;
	private RestTemplate restTemplate;
	
	public GetUserProfileFromCSDNCommand(TraceInfo traceInfo, RestTemplate restTemplate, String token, String applicationId, String guid,
			 CredentialsSource commonSDN) {
		super(traceInfo, HYSTRIX_GROUP_KEY);
		this.traceInfo = traceInfo;
		this.token = token;
		this.applicaitonId = applicationId;
		this.guid = guid;
		this.commonSDN = commonSDN;
		this.restTemplate = restTemplate;
	}

	@Override
	public CommonSDNUserProfileResponse doRun() throws Exception {
		HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-B3-TraceId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getTraceId()));
        requestHeaders.add("X-B3-SpanId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getSpanId()));
        requestHeaders.add("userGuid", guid);
        requestHeaders.add("application-id", applicaitonId);
        requestHeaders.add("Authorization", "Bearer "+token);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        String url = commonSDN.getBaseUri();
        UriComponentsBuilder requestUrl = UriComponentsBuilder.fromHttpUrl(url).path("/users");
        ResponseEntity<CommonSDNUserProfileResponse> response = this.restTemplate.exchange(requestUrl.toUriString(),HttpMethod.GET,new HttpEntity<>(requestHeaders),CommonSDNUserProfileResponse.class);

        logResponse(response);
        return response.getBody();
	}

	private void logResponse(ResponseEntity<CommonSDNUserProfileResponse> response) {
		if (response.getBody().getError() != null) {
        	log.info("Status:" +response.getStatusCode()+ " Error:" + response.getBody().getError());
        }
	}
}
