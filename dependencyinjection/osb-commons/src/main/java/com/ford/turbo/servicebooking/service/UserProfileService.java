package com.ford.turbo.servicebooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.command.GetUserProfileCommand;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;

@Service
public class UserProfileService {

    private final RestTemplate restTemplate;
    private final CredentialsSource credentialSource;
    private final TraceInfo traceInfo;

    @Autowired
    public UserProfileService(@Qualifier("NGSDN_REST_TEMPLATE") RestTemplate restTemplate, @Qualifier("NGSDN") CredentialsSource credentialsSource, TraceInfo traceInfo) {
        this.restTemplate = restTemplate;
        this.credentialSource = credentialsSource;
        this.traceInfo = traceInfo;
    }

    public UserProfile getUserProfile(String authToken, String applicationId) {
        return new GetUserProfileCommand(traceInfo, restTemplate, authToken, applicationId, credentialSource).execute();
    }
}
