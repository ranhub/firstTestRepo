package com.ford.turbo.aposb.common.authsupport.mutualauth;

import com.ford.turbo.aposb.common.authsupport.CredentialsSourceHelper;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class MutualAuthRestTemplateTest {


    @Test
    public void Should_FailToLoadWithException() {
        try {
            CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
            final MutualAuthRestTemplate restTemplate = new MutualAuthRestTemplate(credentialsSource);
            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception e) {
            assertThatExceptionOfType(Exception.class);
        }
    }

    @Test
    public void should_failWithBadUrl_when_credentialsSourceMissingBaseUri() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenCredentialsWithMissingBaseUri();
        try {
            new MutualAuthRestTemplate(credentialsSource);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch(NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("credentials does not contain baseUri");
        }
    }

    @Test
    public void should_failWithBadUrl_when_backendCredentialsSourceMissingBaseUri() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenCredentialsWithMissingBaseUri();
        try {
            new MutualAuthRestTemplate(credentialsSource, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch(NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("backend credentials does not contain baseUri");
        }
    }

    @Test
    public void should_failWithBadUrl_when_hostnameCredentialsSourceMissingBaseUri() throws Exception {
        CredentialsSource backendCredentialsSource = CredentialsSourceHelper.givenValidCredentialsSource();
        CredentialsSource hostnameCredentialsSource = CredentialsSourceHelper.givenCredentialsWithMissingBaseUri();
        try {
            new MutualAuthRestTemplate(backendCredentialsSource, hostnameCredentialsSource);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch(NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("hostname credentials does not contain baseUri");
        }
    }

}