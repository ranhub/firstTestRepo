package com.ford.turbo.aposb.common.authsupport.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthResponseStatus;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenExpiredException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenFailedException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AppIdNotFoundException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotFoundException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;
import com.ford.turbo.common.sharedtests.AuthTestHelper;
import com.netflix.config.ConfigurationManager;

import lombok.extern.slf4j.Slf4j;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class AuthTokenValidatorTest {

    private static String tokenNa;
    private static String sdnAppIdNa;
    private static String tokenEu;
    private static String sdnAppIdEu;
    private static String tokenAp;
    private static String sdnAppIdAp;

    private CredentialsSource figSource;

    private ObjectFactory<ValidateAuthTokenCommand> commandFactory;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;

    @BeforeClass
    public static void fetchAuthToken() throws IOException {
        AuthTestHelper authTestHelper = new AuthTestHelper(new CredentialsSource("NGSDN"), new CredentialsSource("LIGHTHOUSE"), new CredentialsSource("APPLICATION_ID_MAPPINGS"));
        sdnAppIdNa = authTestHelper.getAppIdFordNA();
        tokenNa = authTestHelper.getToken(sdnAppIdNa);
        assertThat(tokenNa).isNotEmpty();
        assertThat(sdnAppIdNa).isNotEmpty();

        sdnAppIdEu = authTestHelper.getAppIdFordEU();
        tokenEu = authTestHelper.getToken(sdnAppIdEu);
        assertThat(tokenEu).isNotEmpty();
        assertThat(sdnAppIdEu).isNotEmpty();

        sdnAppIdAp = authTestHelper.getAppIdFordAP();
        tokenAp = authTestHelper.getToken(sdnAppIdAp);
        assertThat(tokenAp).isNotEmpty();
        assertThat(sdnAppIdAp).isNotEmpty();
    }

    @Before
    public void setUp() throws IOException {
        Span fakeSpan = Span.builder().build();
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());

        when(mockTracer.getCurrentSpan()).thenReturn(fakeSpan);
        when(mockTracer.close(any())).thenReturn(fakeSpan);

        figSource = new CredentialsSource("FIG_AUTHENTICATION");

        commandFactory =
                () -> new FigAuthTokenValidationCommand(traceInfo, figSource, new RestTemplate());

        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.ValidateAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.FigAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
    }

    @Test
    public void should_notThrowAnythingWhenTokenIsValid() {
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        tokenNa = "PAA/AHgAbQBsACAAdgBlAHIAcwBpAG8AbgA9ACIAMQAuADAAIgAgAGUAbgBjAG8AZABpAG4AZwA9ACIAdQB0AGYALQAxADYAIgA/AD4APABBAEMAPgA8AFQAbwBrAGUAbgBEAGEAdABhAD4APAAhAFsAQwBEAEEAVABBAFsAewAiAEQAaQBzAHAAbABhAHkATgBhAG0AZQAiADoAIgBmAG8AcgBkAC0AdAB1AHIAYgBvAC0AaQBuAHQAZQByAG4AYQBsAC0AYQBwAEAAcABpAHYAbwB0AGEAbAAuAGkAbwAiACwAIgBVAG4AaQBxAHUAZQBBAHUAdABoAEkAZAAiADoAIgBlADAAZQA3AGUANABhAGIALQBmADYAMwAxAC0ANABjAGYAMAAtAGEAMgA5ADUALQAxADkAOAA1AGIANQBhADcAOQA1AGMAMQAiACwAIgBFAHgAdABlAHIAbgBhAGwAVABvAGsAZQBuACIAOgAiADAAMgA3AGYANQBkADMAZAAtAGYAYwBmAGEALQA0ADIAOQA4AC0AOABhADAAZQAtAGUAMAA4AGEANQBmAGIAMQBlADMAMgA5ACIALAAiAFMAZQBzAHMAaQBvAG4ASQBkACIAOgAiADYAYgAyAGIANAA1ADUAYwAtADUAZQA1ADIALQA0ADQANQA3AC0AYgBjADIAMQAtADMAYwA5AGEAZAA4AGMAYgBhADQAYgAxACIALAAiAEQAYQB0AGUASQBzAHMAdQBlAGQAIgA6AC0AOAA1ADgANwAzADgANwA4ADAAMwAyADUAMwA2ADcANQA2ADAANgAsACIASQBzAHMAdQBlAHIAIgA6ACIAbABpAGcAaAB0AGgAbwB1AHMAZQAiACwAIgBWAGEAbABpAGQAYQB0AGUAZAAiADoAIgAyADAAMQA2AC0AMAA1AC0AMAA5AFQAMgAwADoANAAyADoANAAwAC4AMQAxADAAMAAyADAAMgArADAAMAA6ADAAMAAiACwAIgBWAGUAcgBzAGkAbwBuACIAOgAiADEALgAwAC4AMAAiACwAIgBUAFQATAAiADoAMQA0ADQAMAAwAC4AMAAsACIAVQBzAGUAcgBJAGQAIgA6ADMAMAA3ADgALAAiAFMAZABuAEEAcABwAEkAZAAiADoAMwB9AF0AXQA+ADwALwBUAG8AawBlAG4ARABhAHQAYQA+ADwAUwBpAGcAbgBhAHQAdQByAGUAPgA8ACEAWwBDAEQAQQBUAEEAWwBxAE0ARgBkAEgASABHAHMAbABQAGwATwBlACsAegA5AGUAWQBwAEEASQBvAEsAWgBtAGcAKwBjADEAdwB6AGEAQwAxADUAegAwAFQAMABXAEcAWgBZAD0AXQBdAD4APAAvAFMAaQBnAG4AYQB0AHUAcgBlAD4APAAvAEEAQwA+AA==";
        sdnAppIdNa = "601DEC23-B3C5-49DD-9E29-2A6F0EDEC685";
        authTokenValidator.checkAuthToken(tokenNa);
        authTokenValidator.checkAppId(sdnAppIdNa);
    }

    @Test
    public void should_workEvenIfThereAreNewFieldsInFIGResponse(){
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        ObjectFactory<ValidateAuthTokenCommand> commandFactoryForMocking =
                () -> new FigAuthTokenValidationCommand(traceInfo, figSource, mockRestTemplate);
        String response = "{\"Profile\":{\"SdnAppId\":\"E0796968-B476-46B9-8B2C-A50241B7FEF3\",\"ProviderIdentity\":\"c908f26d-d3fb-40af-8952-cc980921459e\",\"IdentityProvider\":\"lighthouse\"},\"Status\":1,\"Version\":\"1\"}";
        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactoryForMocking, null);
        authTokenValidator.checkValid(tokenNa, sdnAppIdNa);
    }

    @Test
    public void should_returnUserInfoWhenTokenIsValid_NA() {
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenNa, sdnAppIdNa);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }

    @Test
    public void should_returnUserInfoWhenTokenIsValid_AP() {
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenAp, sdnAppIdAp);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }

    @Test
    public void should_returnUserInfoWhenTokenIsValid_EU() {
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
        UserIdentity userProfile = authTokenValidator.checkValid(tokenEu, sdnAppIdEu);
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getLightHouseGuid()).isNotEmpty();
    }

    @Test
    public void should_throwFigAuthTokenExpiredException_when_authenticatingAgainstFig_withExpiredAuthToken() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkValid(getWellFormedExpiredToken(), sdnAppIdNa);
            failBecauseExceptionWasNotThrown(FigAuthTokenExpiredException.class);
        } catch (Exception e) {
            assertThat(e).as("Expected FigAuthTokenExpiredException but got %s", e.getClass()).isInstanceOf(FigAuthTokenExpiredException.class);
        }
    }

    @Test
    public void should_FailWhenAppIdIsMissing() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkValid(tokenNa, null);
            failBecauseExceptionWasNotThrown(AppIdNotFoundException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AppIdNotFoundException.class);
            AppIdNotFoundException exception = (AppIdNotFoundException) e;
            Assertions.assertThat(exception.getFordError().getStatusContext()).isEqualTo(StatusContext.HTTP.getStatusContext());
            Assertions.assertThat(exception.getFordError().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            Assertions.assertThat(exception.getFordError().getMessage()).isEqualTo("Authorization has been denied for this request. App Id could be missing.");
        }
    }

    @Test
    public void should_FailWhenAppIdIsEmpty() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkValid(tokenNa, "");
            failBecauseExceptionWasNotThrown(AppIdNotFoundException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(AppIdNotFoundException.class);
            AppIdNotFoundException exception = (AppIdNotFoundException) e;
            Assertions.assertThat(exception.getFordError().getStatusContext()).isEqualTo(StatusContext.HTTP.getStatusContext());
            Assertions.assertThat(exception.getFordError().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            Assertions.assertThat(exception.getFordError().getMessage()).isEqualTo("Authorization has been denied for this request. App Id could be missing.");
        }
    }

    @Test
    public void should_FailWhenAppIdIsUnknown() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkValid(tokenNa, "unknownAppId");
            failBecauseExceptionWasNotThrown(FigAuthTokenFailedException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(FigAuthTokenFailedException.class);
            FigAuthTokenFailedException exception = (FigAuthTokenFailedException) e;
            Assertions.assertThat(exception.getFordError().getStatusContext()).isEqualTo(StatusContext.FIG.getStatusContext());
            Assertions.assertThat(exception.getFordError().getStatusCode()).isEqualTo(FigAuthResponseStatus.FAILED);
            Assertions.assertThat(exception.getFordError().getMessage()).isEqualTo("Auth token validation failed");
        }
    }
    
    @Test
    public void should_notThrowExceptionWhenAppIdIsValid() {
    	ContinentCodeExtractor extract = mock(ContinentCodeExtractor.class);
    	when(extract.getContinent("VALID-APP-ID")).thenReturn(ContinentCode.NA);
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(null, extract);
        authTokenValidator.checkValidAppId("VALID-APP-ID");
    }
    
    @Test
    public void should_FailWhenAppIdIsNotValid() {
    	expectedException.expect(UnknownAppIdException.class);
    	expectedException.expectMessage("Unknown application id INVALID-APP-ID");
    	
    	ContinentCodeExtractor extract = mock(ContinentCodeExtractor.class);
    	when(extract.getContinent("INVALID-APP-ID")).thenReturn(null);
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(null, extract);
        authTokenValidator.checkValidAppId("INVALID-APP-ID");
    }
    
    @Test
    public void should_FailWhenAppIdIsNotValid_2() {
    	expectedException.expect(UnknownAppIdException.class);
    	expectedException.expectMessage("Unknown application id INVALID-APP-ID");
    	
    	ContinentCodeExtractor extract = mock(ContinentCodeExtractor.class);
    	when(extract.getContinent("INVALID-APP-ID")).thenThrow(new UnknownAppIdException("INVALID-APP-ID"));
        AuthTokenValidator authTokenValidator = new AuthTokenValidator(null, extract);
        authTokenValidator.checkValidAppId("INVALID-APP-ID");
    }

    @Test
    public void should_throwFigAuthTokenFailedException_whenAuthenticatingAgainstFig_withMalformedToken() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkValid(AuthTestHelper.getMalformedXmlToken(), sdnAppIdNa);
            failBecauseExceptionWasNotThrown(FigAuthTokenFailedException.class);
        } catch (Exception e) {
            assertThat(e).as("Expected FigAuthTokenFailedException but got %s", e.getClass()).isInstanceOf(FigAuthTokenFailedException.class);
        }
    }

    @Test
    public void should_returnErrorWhenTokenIsMissing() {
        try {
            AuthTokenValidator authTokenValidator = new AuthTokenValidator(commandFactory, null);
            authTokenValidator.checkAuthToken(null);
            failBecauseExceptionWasNotThrown(AuthTokenNotFoundException.class);
        } catch (Exception e) {
            assertThat(e).as("Expected AuthTokenNotFoundException but got %s", e.getClass()).isInstanceOf(AuthTokenNotFoundException.class);
        }
    }
    
    String getWellFormedExpiredToken() {
    	String expiredToken  = System.getenv("EXPIRED_TOKEN");
        log.info("Expired token from env "+expiredToken);
        return expiredToken;
    }
}
