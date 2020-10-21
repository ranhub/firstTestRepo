package com.ford.turbo.aposb.common.authsupport.mutualauth;

import java.util.Objects;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutualAuthWebServiceTemplate extends WebServiceTemplate {

    @Autowired
    public MutualAuthWebServiceTemplate(CredentialsSource credentialsSource) {
        super();

        String baseUrl = Objects.requireNonNull(credentialsSource.getBaseUri(), "credentials does not contain baseUri");
        setDefaultUri(baseUrl);
        HttpClient httpClient = MutualAuthHelper.initHttpClient(credentialsSource, null);
        log.debug("Created HTTP Client that can send client certs: " + httpClient);

        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
        final WebServiceMessageSender[] senders = {messageSender};
        setMessageSenders(senders);
    }

    @Autowired
    public MutualAuthWebServiceTemplate(CredentialsSource backendCredentialsSource, CredentialsSource hostnameCredentialsSource) {
        super();

        String baseUrl = Objects.requireNonNull(backendCredentialsSource.getBaseUri(), "backend credentials does not contain baseUri");
        Objects.requireNonNull(hostnameCredentialsSource.getBaseUri(), "hostname credentials does not contain baseUri");
        setDefaultUri(baseUrl);
        HttpClient httpClient = MutualAuthHelper.initHttpClient(backendCredentialsSource, hostnameCredentialsSource);
        log.debug("Created HTTP Client that can send client certs: " + httpClient);

        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
        final WebServiceMessageSender[] senders = {messageSender};
        setMessageSenders(senders);
    }

}
