package com.ford.turbo.aposb.common.authsupport.azure;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

public class AzureADTokenCommand extends TimedHystrixCommand<AzureADToken> {

    private RestTemplate restTemplate;
    private CredentialsSource azureADCredentials;

    @Autowired
    public AzureADTokenCommand(TraceInfo traceInfo,
                               RestTemplate restTemplate,
                               CredentialsSource azureADCredentials,
                               String hystrixGroupKey) {
        super(traceInfo, hystrixGroupKey);
        this.restTemplate = restTemplate;
        this.azureADCredentials = azureADCredentials;
    }

    @Override
    public AzureADToken doRun() throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(azureADCredentials.getBaseUri());

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("client_id", azureADCredentials.getExtraCredentials().get("clientId").toString());
        multiValueMap.add("resource", azureADCredentials.getExtraCredentials().get("resource").toString());
        multiValueMap.add("grant_type", azureADCredentials.getExtraCredentials().get("grant_type").toString());
        multiValueMap.add("client_secret", azureADCredentials.getExtraCredentials().get("clientSecret").toString());
        
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cache-Control", "no-cache");
        
        ResponseEntity<AzureADTokenResponse> responseEntity = restTemplate.postForEntity(uriBuilder.build(), new HttpEntity<>(multiValueMap, requestHeaders), AzureADTokenResponse.class);
        LocalDateTime expireDate = extractExpireDate(responseEntity.getBody());
        return new AzureADToken(responseEntity.getBody().getAccess_token(), expireDate);
    }

    private LocalDateTime extractExpireDate(AzureADTokenResponse azureADTokenResponse) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(azureADTokenResponse.getExpires_on())), ZoneId.systemDefault());
    }
    
    @Data
    public static class AzureADTokenResponse {
    	String expires_on;
    	String access_token;
    }
}
