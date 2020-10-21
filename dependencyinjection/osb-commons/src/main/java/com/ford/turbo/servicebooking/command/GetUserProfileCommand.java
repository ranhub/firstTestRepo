package com.ford.turbo.servicebooking.command;

import static org.springframework.util.StringUtils.isEmpty;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.ford.turbo.servicebooking.exception.CustomerIdNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfileResponse;
import com.ford.turbo.servicebooking.utils.Utilities;

public class GetUserProfileCommand extends TimedHystrixCommand<UserProfile> {
    private final RestTemplate restTemplate;
    private final String authToken;
    private final String baseUrl;
    private final String appId;

    public GetUserProfileCommand(@NotNull TraceInfo traceInfo, RestTemplate restTemplate, String authToken, String appId, CredentialsSource ngsdnCredentialsSource) {
        super(traceInfo, "GetUserProfileCommand");
        this.restTemplate = restTemplate;
        this.authToken = authToken;
        this.baseUrl = ngsdnCredentialsSource.getExtraCredentials().get("baseUri").toString();
        this.appId = isEmpty(appId) ? null : appId;
    }

    @Override
    public UserProfile doRun() throws Exception {
        HttpHeaders requestHeaders = getRequestHeaders();

        String url = baseUrl + "/users";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("lrdt", LocalDate.now().toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<UserProfileResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, UserProfileResponse.class);

        if (response.getBody() != null && !((Integer) HttpStatus.OK.value()).equals(response.getBody().getStatus())) {
            throw new CustomerIdNotFoundException();
        }

        return response.getBody().getProfile();
    }

    public HttpHeaders getRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("auth-token", authToken);
        if (appId != null) {
            requestHeaders.add("Application-Id", appId);
        }
        Utilities.populateRequestTraceForCommand(requestHeaders, this);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }
}
