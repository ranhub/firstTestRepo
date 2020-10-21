package com.ford.turbo.aposb.common.authsupport.loadtest;

import java.util.Objects;

import com.ford.turbo.aposb.common.authsupport.validator.ValidateAuthTokenCommand;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotAuthorizedException;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;

@Component
@Profile("loadtest")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoadTestAuthTokenValidationCommand extends ValidateAuthTokenCommand {

    private long delay;
    private String authToken;

    @Autowired
    public LoadTestAuthTokenValidationCommand(
            TraceInfo traceInfo,
            @Value("${loadtest.LoadTestAuthTokenValidationCommand.delay}") long delay) {
        super(traceInfo);
        this.delay = delay;
    }

    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void setAppId(String appId) {
        // do nothing, don't need it for now
    }

    @Override
    public UserIdentity performValidation() throws AuthTokenNotAuthorizedException {
        try {
            System.out.println("sleeptimeProperty=" + delay);
            Objects.requireNonNull(authToken);
            Thread.sleep(delay);

            UserIdentity userProfile = new UserIdentity("fakeLightHouseToken");

            return userProfile;
        } catch (IllegalArgumentException | InterruptedException e) {
            throw new AuthTokenNotAuthorizedException("Load Test failure - could not authenticate token", e);
        }
    }
}
