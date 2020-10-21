package com.ford.turbo.aposb.common.authsupport.validator;

import java.io.IOException;
import java.util.Objects;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthResponseStatus;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenExpiredException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenFailedException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenRevokedException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadGatewayException;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.authsupport.loadtest.LoadTestAuthTokenValidationCommand;
import com.ford.turbo.aposb.common.authsupport.models.FigResponse;
import com.ford.turbo.aposb.common.authsupport.models.SdnTokenUtility;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnMissingBean({LoadTestAuthTokenValidationCommand.class})
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FigAuthTokenValidationCommand extends ValidateAuthTokenCommand {

    private static final String HYSTRIX_GROUP_KEY = "FIG";

    private final String APPLICATION_ID;
    private final String FIG_SERVICE_URI;

    private final RestTemplate restTemplate;
    private final CredentialsSource figCredentials;

    private String authToken;
    private String appId;

    @Autowired
    public FigAuthTokenValidationCommand(
            TraceInfo traceInfo,
            @Qualifier("FIG_AUTHENTICATION") CredentialsSource figCredentials,
            RestTemplate restTemplate) {
        super(traceInfo, HYSTRIX_GROUP_KEY);
        this.restTemplate = restTemplate;
        this.figCredentials = figCredentials;
        this.APPLICATION_ID = figCredentials.getExtraCredentials().get("applicationId").toString();
        this.FIG_SERVICE_URI = figCredentials.getExtraCredentials().get("figEndpoint").toString();
    }

    @Override
    public UserIdentity performValidation() {
        Objects.requireNonNull(authToken);

        String accessToken = new AcquireAzureAccessTokenCommand(getTraceInfo(), figCredentials).execute();
        UserIdentity userProfile = acquireLightHouseGuidViaFig(accessToken);
        if (userProfile != null) {
            log.debug("User : " + userProfile.getLightHouseGuid() + " successfully validated.");
        }
        return userProfile;
    }

    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void setAppId(String appId) {
        this.appId = appId;
    }

    protected UserIdentity acquireLightHouseGuidViaFig(String accessToken) {
        HttpHeaders requestHeaders = activeDirectoryRequestHttpHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(SdnTokenUtility.serializedSdnToken(authToken, appId), requestHeaders);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(FIG_SERVICE_URI, HttpMethod.POST, entity, String.class);
        } catch(HttpClientErrorException e) {
            throw new BadGatewayException("FIG request failed: ", e);
        }

        HttpStatus httpStatus = response.getStatusCode();
        if (httpStatus == HttpStatus.OK) {
            FigResponse figResponse;
            try {
                figResponse = new ObjectMapper().readValue(response.getBody(), FigResponse.class);
            } catch (IOException e) {
                throw new BadGatewayException("Unable to parse response from FIG: " + response.getBody(), e);
            }
            handleFigResponseErrors(figResponse.Status);
            return figResponse.Profile;
        } else {
            throw new BadGatewayException("Got " + httpStatus.value() + " - " + httpStatus.getReasonPhrase());
        }
    }

    private void handleFigResponseErrors(int activeDirectoryStatusCode) {

        switch (activeDirectoryStatusCode) {
            case FigAuthResponseStatus.SUCCESS:
                //no action needed
                break;
            case FigAuthResponseStatus.FAILED:
                throw new FigAuthTokenFailedException();
            case FigAuthResponseStatus.EXPIRED:
                throw new FigAuthTokenExpiredException();
            case FigAuthResponseStatus.REVOKED:
                throw new FigAuthTokenRevokedException();
        }
    }

    private HttpHeaders activeDirectoryRequestHttpHeaders(String accessToken) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Application-Id", APPLICATION_ID);
        requestHeaders.add("Authorization", "Bearer " + accessToken);
        requestHeaders.add("Accept", "application/json;char-set=utf-8");
        requestHeaders.add("Content-Type", "application/json;char-set=utf-8");

        return requestHeaders;
    }
}
