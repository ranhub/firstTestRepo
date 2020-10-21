package com.ford.turbo.servicebooking.mutualauth;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

public class MutualAuthRestTemplate extends RestTemplate {

    @Autowired
    public MutualAuthRestTemplate(CredentialsSource credentialsSource) {
        super();
        Objects.requireNonNull(credentialsSource.getBaseUri(), "credentials does not contain baseUri");
    }
}
