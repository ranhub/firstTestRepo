package com.ford.turbo.aposb.common.authsupport.validator;

import com.ford.turbo.aposb.common.basemodels.annotations.AcceptanceTest;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.common.sharedtests.AuthTestHelper;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;
import com.netflix.config.ConfigurationManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
@Category(AcceptanceTest.class)
public class FigMonitoringTest {

    private static CredentialsSource figSource;
    private static AuthTestHelper authTestHelper;
    private static ObjectFactory<ValidateAuthTokenCommand> commandFactory;
    private static TraceInfo traceInfo;

    @BeforeClass
    public static void fetchAuthToken() throws IOException {
        authTestHelper = new AuthTestHelper(new CredentialsSource("NGSDN"), new CredentialsSource("LIGHTHOUSE"), new CredentialsSource("APPLICATION_ID_MAPPINGS"));
        traceInfo = new TraceInfo(mock(Tracer.class), new TraceKeys());

        figSource = new CredentialsSource("FIG_AUTHENTICATION");
        commandFactory =
                () -> new FigAuthTokenValidationCommand(traceInfo, figSource, new RestTemplate());
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.ValidateAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.FigAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
    }

    @Test
    public void should_acquireAzureAccessToken() {
        String accessToken = new AcquireAzureAccessTokenCommand(traceInfo, figSource).execute();
        assertThat(accessToken).isNotEmpty();
    }

    @Test
    public void should_validateApAppId() {
        String sdnAppIdAp = authTestHelper.getAppIdFordAP();
        String tokenAp = authTestHelper.getToken(sdnAppIdAp);

        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenAp, sdnAppIdAp);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }

    @Test
    public void should_validateNaAppId() {
        String sdnAppIdNa = authTestHelper.getAppIdFordNA();
        String tokenNa = authTestHelper.getToken(sdnAppIdNa);

        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenNa, sdnAppIdNa);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }

    @Test
    public void should_validateEuAppId() {
        String sdnAppIdEu = authTestHelper.getAppIdFordEU();
        String tokenEu = authTestHelper.getToken(sdnAppIdEu);

        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenEu, sdnAppIdEu);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }
}
