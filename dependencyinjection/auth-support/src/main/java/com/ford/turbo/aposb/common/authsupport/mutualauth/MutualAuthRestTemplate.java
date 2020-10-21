package com.ford.turbo.aposb.common.authsupport.mutualauth;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class MutualAuthRestTemplate extends RestTemplate {

    @Autowired
    public MutualAuthRestTemplate(CredentialsSource credentialsSource) {
        super();
        Objects.requireNonNull(credentialsSource.getBaseUri(), "credentials does not contain baseUri");

        HttpClient httpClient = MutualAuthHelper.initHttpClient(credentialsSource, null);
        setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Autowired
    public MutualAuthRestTemplate(CredentialsSource backendCredentialsSource, CredentialsSource hostnameCredentialsSource) {
        super();
        Objects.requireNonNull(backendCredentialsSource.getBaseUri(), "backend credentials does not contain baseUri");
        Objects.requireNonNull(hostnameCredentialsSource.getBaseUri(), "hostname credentials does not contain baseUri");

        HttpClient httpClient = MutualAuthHelper.initHttpClient(backendCredentialsSource, hostnameCredentialsSource);
        setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
