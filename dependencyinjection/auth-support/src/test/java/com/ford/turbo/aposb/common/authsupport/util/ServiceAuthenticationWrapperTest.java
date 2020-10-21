package com.ford.turbo.aposb.common.authsupport.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;


@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class ServiceAuthenticationWrapperTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void creating_ServiceAuthenticationWrapper_testValidAuthorizationTypes()
    {
        ServiceAuthenticationWrapper wrapperOAuth = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2);
        assertThat(wrapperOAuth.getAuthenticationMethod()).isEqualTo(ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2);

        ServiceAuthenticationWrapper wrapperFig = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
        assertThat(wrapperFig.getAuthenticationMethod()).isEqualTo(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
    }

    @Test
    public void setting_AuthenticationMethod_getterReturnsSame() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
        wrapper.setAuthenticationMethod(ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2);
        assertThat(ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2).isEqualTo(wrapper.getAuthenticationMethod());

    }

    @Test
    public void setting_Oauth2_token_worksAsExpected() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper((ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2));
        String token = "My Token";
        wrapper.setOauthBearerToken(token);
        assertThat(token).isEqualTo(wrapper.getOauthBearerToken());
    }

    @Test
    public void setting_Oauth2_token_For_FIG_failsAsExpected() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper((ServiceAuthenticationWrapper.AuthenticationMethods.FIG));
        String token = "My Token";
        exception.expect(IllegalStateException.class);
        wrapper.setOauthBearerToken(token);
    }

    @Test
    public void setting_applicationId_worksAsExpected() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper((ServiceAuthenticationWrapper.AuthenticationMethods.FIG));
        String appId = "app id";
        wrapper.setApplicationId(appId);
        assertThat(appId).isEqualTo(wrapper.getApplicationId());
    }

    @Test
    public void setting_FIG_authToken_worksAsExpected() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper((ServiceAuthenticationWrapper.AuthenticationMethods.FIG));
        String authToken = "auth token";
        wrapper.setFigAuthorizationToken(authToken);
        assertThat(authToken).isEqualTo(wrapper.getFigAuthorizationToken());
    }

    @Test
    public void setting_FIG_authToken_for_OAUTH_failsAsExpected() {
        ServiceAuthenticationWrapper wrapper = new ServiceAuthenticationWrapper((ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2));
        String authToken = "auth token";
        exception.expect(IllegalStateException.class);
        wrapper.setFigAuthorizationToken(authToken);
    }

    @Test
    public void checking_equality_for_equal_serviceAuthenticationWrappers() {
        ServiceAuthenticationWrapper wrapper1 = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
        ServiceAuthenticationWrapper wrapper2 = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
        wrapper1.setApplicationId("APP_ID");
        wrapper2.setApplicationId("APP_ID");
        wrapper1.setFigAuthorizationToken("TOKEN");
        wrapper2.setFigAuthorizationToken("TOKEN");
        assertThat(wrapper1).isEqualTo(wrapper2);
    }

    @Test
    public void checking_equality_for_not_equal_serviceAuthenticationWrappers() {
        ServiceAuthenticationWrapper wrapper1 = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.FIG);
        ServiceAuthenticationWrapper wrapper2 = new ServiceAuthenticationWrapper(ServiceAuthenticationWrapper.AuthenticationMethods.OAUTH2);
        wrapper1.setApplicationId("APP_ID1");
        wrapper2.setApplicationId("APP_ID2");
        assertThat(wrapper1).isNotEqualTo(wrapper2);
    }

}