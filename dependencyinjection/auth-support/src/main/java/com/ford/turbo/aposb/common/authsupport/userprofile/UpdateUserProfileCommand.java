package com.ford.turbo.aposb.common.authsupport.userprofile;

import java.io.IOException;
import java.time.LocalDate;

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
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

public class UpdateUserProfileCommand extends TimedHystrixCommand<UserProfile> {

    private final RestTemplate restTemplate;
    private final String authToken;
    private final String appId;
    private final UserProfile userProfile;
    private final TraceInfo traceInfo;
    private final ContinentCodeExtractor continentCodeExtractor;

    private static final String HYSTRIX_GROUP_KEY = "UpdateUserProfileCommand";

    public UpdateUserProfileCommand(ContinentCodeExtractor continentCodeExtractor,
                                       TraceInfo traceInfo,
                                       RestTemplate restTemplate, String appId, String authToken,
                                       UserProfile userProfile) {
        super(traceInfo, HYSTRIX_GROUP_KEY);
        this.traceInfo = traceInfo;
        this.restTemplate = restTemplate;
        this.appId = appId;
        this.authToken = authToken;
        this.userProfile = userProfile;
        this.continentCodeExtractor = continentCodeExtractor;
    }

    @Override
    public UserProfile doRun() throws Exception {

        HttpHeaders requestHeaders = getRequestHeaders();

        CredentialsSource credentialsSource = getCredentialsSource();

        String url = credentialsSource.getBaseUri() + "/users";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("lrdt", LocalDate.now().toString());

        HttpEntity<UserProfile> entity = new HttpEntity<>(userProfile, requestHeaders);

        ResponseEntity<UserProfile> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entity, UserProfile.class);

        return responseEntity.getBody();
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
