package com.ford.turbo.aposb.common.authsupport.mutualauth;

import com.ford.turbo.aposb.common.authsupport.CredentialsSourceHelper;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class HostnameVerifierFactoryTest {

    @Test
    public void should_createACustomHostnameVerifier_when_credentialsSourceIsNotNull() throws IOException {
        HostnameVerifier hostnameVerifier = HostnameVerifierFactory.createCustomHostnameVerifier(CredentialsSourceHelper.givenValidCredentialsSource());
        assertThat(hostnameVerifier).isInstanceOf(HostnameVerifier.class);
    }

    @Test
    public void should_createACustomHostnameVerifier_when_credentialsSourceIsNull() {
        HostnameVerifier hostnameVerifier = HostnameVerifierFactory.createCustomHostnameVerifier(null);
        assertThat(hostnameVerifier).isInstanceOf(HostnameVerifier.class);
    }
}