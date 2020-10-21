package com.ford.turbo.aposb.common.authsupport.mutualauth;

import com.ford.turbo.aposb.common.authsupport.CredentialsSourceHelper;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.junit.Test;
import org.springframework.ws.transport.WebServiceMessageSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class MutualAuthWebServiceTemplateTest {

    @Test
    public void should_loadAndPassInKeystoreInfo() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenValidCredentialsSource();
        MutualAuthWebServiceTemplate mutualAuthWebServiceTemplate = new MutualAuthWebServiceTemplate(credentialsSource);

        WebServiceMessageSender[] messageSenders = mutualAuthWebServiceTemplate.getMessageSenders();
        assertThat(messageSenders).isNotNull().hasSize(1);
        assertThat(mutualAuthWebServiceTemplate.getDefaultUri()).isEqualTo("https://testbaseurl");
    }

    @Test
    public void should_failToLoadWithException() {
        try {
            CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
            MutualAuthWebServiceTemplate mutualAuthWebServiceTemplate = new MutualAuthWebServiceTemplate(credentialsSource);

            mutualAuthWebServiceTemplate.getMessageSenders();
            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception e) {
            assertThatExceptionOfType(Exception.class);
        }
    }

    @Test
    public void should_failWithBadUrl_when_credentialsSourceMissingBaseUri() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenCredentialsWithMissingBaseUri();
        try {
            new MutualAuthWebServiceTemplate(credentialsSource);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch(NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("credentials does not contain baseUri");
        }
    }

    @Test
    public void should_failWithBadUrl_when_backendCredentialsSourceMissingBaseUri() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenCredentialsWithMissingBaseUri();
        try {
            new MutualAuthWebServiceTemplate(credentialsSource, null);
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
            new MutualAuthWebServiceTemplate(backendCredentialsSource, hostnameCredentialsSource);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch(NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("hostname credentials does not contain baseUri");
        }
    }
}