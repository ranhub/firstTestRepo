package com.ford.turbo.aposb.common.authsupport.validator;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.ActiveDirectoryBearerTokenException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AcquireAzureAccessTokenCommandTest {

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;

    private AcquireAzureAccessTokenCommand command;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** This span will be returned as current by our mock mockTracer. */
    private Span parentSpan;

    @Before
    public void setup() throws IOException {
        when(mockTracer.getCurrentSpan()).thenReturn(parentSpan);
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());
        parentSpan = Span.builder().begin(System.currentTimeMillis()).name("parentSpan").traceId(100).spanId(200).build();

        CredentialsSource credentialsSource = new CredentialsSource("FIG_AUTHENTICATION");

        command = new AcquireAzureAccessTokenCommand(traceInfo, credentialsSource);
        command.clearCache();
    }

    @Test
    public void should_extendTimedHystrixCommand() throws IOException {
        assertThat(TimedHystrixCommand.class.isAssignableFrom(AcquireAzureAccessTokenCommand.class)).isTrue();
    }

    @Test
    public void should_joinTrace_and_closeSpan_when_networkCallIsMade() throws Exception {
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(null);
        command.run();
        verify(mockTracer, times(1)).createSpan(anyString(), any(Span.class));
        verify(mockTracer, times(1)).close(any(Span.class));
    }

    @Test
    public void should_makeNetworkCall_when_atomicRefIsEmpty() throws Exception {
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(null);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        spy.run();
        verify(spy, times(1)).getAuthenticationResult();
    }

    @Test
    public void should_makeNetworkCall_when_expired() throws Exception {
        AuthenticationResult authenticationResult = new AuthenticationResult("", "", "", -60 * 10, "", null, false);
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(authenticationResult);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        spy.run();
        verify(spy, times(1)).getAuthenticationResult();
    }

    @Test
    public void should_makeNetworkCall_when_expiringIn5Minutes() throws Exception {
        AuthenticationResult authenticationResult = new AuthenticationResult("", "", "", 60 * 5, "", null, false);
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(authenticationResult);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        spy.run();
        verify(spy, times(1)).getAuthenticationResult();
    }

    @Test
    public void should_notMakeNetworkCall_when_expiringIn6Minutes() throws Exception {
        AuthenticationResult authenticationResult = new AuthenticationResult("", "", "", 60 * 6, "", null, false);
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(authenticationResult);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        spy.run();
        verify(spy, times(0)).getAuthenticationResult();
    }

    @Test
    public void should_reuseOldAccessToken() throws Exception {
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(null);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        spy.run();
        spy.run();
        verify(spy, times(1)).getAuthenticationResult();
        assertThat(AcquireAzureAccessTokenCommand.authenticationResultRef.get()).isNotNull();
    }

    @Test
    public void should_resetReference_when_anExceptionOccursDuringNetworkCall() throws Exception {
        AuthenticationResult authenticationResult = new AuthenticationResult("", "", "", 60 * 5, "", null, false);
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(authenticationResult);
        AcquireAzureAccessTokenCommand spy = Mockito.spy(command);
        when(spy.getAuthenticationResult()).thenThrow(MalformedURLException.class);

        try {
            spy.run();
        } catch (Exception e) {
            // exception is expected
        }

        assertThat(AcquireAzureAccessTokenCommand.authenticationResultRef.get()).isNull();
    }

    @Test
    public void should_resetReference_when_clearCache() {
        AuthenticationResult authenticationResult = new AuthenticationResult("", "", "", 60 * 5, "", null, false);
        AcquireAzureAccessTokenCommand.authenticationResultRef.set(authenticationResult);
        AcquireAzureAccessTokenCommand.clearCache();
        assertThat(AcquireAzureAccessTokenCommand.authenticationResultRef.get()).isNull();
    }

    @Test
    public void should_throwActiveDirectoryBearerTokenException_when_unexpectedResponseFromAzureAD() throws IOException {
        CredentialsSource credentialsSource = new CredentialsSource("FIG_AUTHENTICATION");
        credentialsSource.getExtraCredentials().put("activeDirectoryEndpoint", "invalidUrl");
        command = new AcquireAzureAccessTokenCommand(traceInfo, credentialsSource);

        expectedException.expect(HystrixRuntimeException.class);
        expectedException.expectCause(instanceOf(ActiveDirectoryBearerTokenException.class));
        expectedException.expectCause(hasProperty("message", startsWith("Failed to obtain new bearer token:")));
        expectedException.expectMessage("AcquireAzureAccessTokenCommand failed and no fallback available.");

        command.execute();
    }
}
