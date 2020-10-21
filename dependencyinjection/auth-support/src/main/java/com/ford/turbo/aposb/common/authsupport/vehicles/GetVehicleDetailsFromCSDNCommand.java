package com.ford.turbo.aposb.common.authsupport.vehicles;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetVehicleDetailsFromCSDNCommand extends TimedHystrixCommand<CommonSDNUserVehicleResponse> {
	
	private static final String HYSTRIX_GROUP_KEY = "GetVehicleDetailsFromCSDNCommand";
	private final TraceInfo traceInfo;
	private String token;
	private String applicationId;
	private String guid;
	private String vin;
	private CredentialsSource commonSDN;
	private RestTemplate restTemplate;
	
	public GetVehicleDetailsFromCSDNCommand(TraceInfo traceInfo, RestTemplate restTemplate, String vin, String token, String applicationId, String guid,
			 CredentialsSource commonSDN) {
		super(traceInfo, HYSTRIX_GROUP_KEY);
		this.traceInfo = traceInfo;
		this.restTemplate = restTemplate;
		this.vin = vin;
		this.token = token;
		this.applicationId = applicationId;
		this.guid = guid;
		this.commonSDN = commonSDN;
	}

	@Override
	public CommonSDNUserVehicleResponse doRun() throws Exception {
		HttpHeaders requestHeaders = new HttpHeaders();
        
        requestHeaders.add("X-B3-TraceId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getTraceId()));
        requestHeaders.add("X-B3-SpanId", String.valueOf(traceInfo.getTracer().getCurrentSpan().getSpanId()));
        requestHeaders.add("userGuid", guid);
        requestHeaders.add("application-id", applicationId);
        requestHeaders.add("Authorization", "Bearer "+token);
        
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        String url = commonSDN.getBaseUri();
        
        UriComponentsBuilder requestUrl = UriComponentsBuilder.fromHttpUrl(url).path("/users/vehicles/"+vin+"/detail");
        
        ResponseEntity<CommonSDNUserVehicleResponse> response = this.restTemplate.exchange(requestUrl.toUriString(),HttpMethod.GET,new HttpEntity<>(requestHeaders),CommonSDNUserVehicleResponse.class);

        CommonSDNUserVehicleResponse csdnUserProfile= response.getBody();

        if(csdnUserProfile!=null && csdnUserProfile.getError() != null) {
        	logErrorResponse(response.getStatusCode(),response.getHeaders(),csdnUserProfile);
        }
        return csdnUserProfile;
	}

	private void logErrorResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, Object body) {
		 log.error("Response Status Code: " + httpStatus+ " headers "+httpHeaders+ " body "+body);
	}
}
