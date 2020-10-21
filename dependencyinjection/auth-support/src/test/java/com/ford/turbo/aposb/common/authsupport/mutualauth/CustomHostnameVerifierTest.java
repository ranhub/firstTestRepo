package com.ford.turbo.aposb.common.authsupport.mutualauth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomHostnameVerifierTest {

    @Mock
    private HostnameVerifier mockBaseHostnameVerifier;

    @Test
    public void should_returnTrue_when_baseHostnameVerified() {
        String hostname = "someHostname";
        when(mockBaseHostnameVerifier.verify(eq(hostname), any(SSLSession.class))).thenReturn(true);

        CustomHostnameVerifier customHostnameVerifier = new CustomHostnameVerifier("verifiedHostname", mockBaseHostnameVerifier);

        assertThat(customHostnameVerifier.verify(hostname, mock(SSLSession.class))).isTrue();
    }

    @Test
    public void should_returnFalse_when_hostnameIsNull() {
        String hostname = null;
        when(mockBaseHostnameVerifier.verify(eq(hostname), any(SSLSession.class))).thenReturn(false);

        CustomHostnameVerifier customHostnameVerifier = new CustomHostnameVerifier("verifiedHostname", mockBaseHostnameVerifier);

        assertThat(customHostnameVerifier.verify(hostname, mock(SSLSession.class))).isFalse();
    }

    @Test
    public void should_returnFalse_when_baseHostnameIsNotVerifiedAndIsNotKnown() {
        String hostname = "someHostname";
        when(mockBaseHostnameVerifier.verify(eq(hostname), any(SSLSession.class))).thenReturn(false);

        CustomHostnameVerifier customHostnameVerifier = new CustomHostnameVerifier("verifiedHostname", mockBaseHostnameVerifier);

        assertThat(customHostnameVerifier.verify(hostname, mock(SSLSession.class))).isFalse();
    }

    @Test
    public void should_returnTrue_when_hostnameIsNotNullAndHostnameIsKnown() {
        String hostname = "knownHostname";
        when(mockBaseHostnameVerifier.verify(eq(hostname), any(SSLSession.class))).thenReturn(false);

        CustomHostnameVerifier customHostnameVerifier = new CustomHostnameVerifier("knownHostname", mockBaseHostnameVerifier);

        assertThat(customHostnameVerifier.verify(hostname, mock(SSLSession.class))).isTrue();
    }
}