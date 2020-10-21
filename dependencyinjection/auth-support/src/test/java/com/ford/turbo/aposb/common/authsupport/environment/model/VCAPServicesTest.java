package com.ford.turbo.aposb.common.authsupport.environment.model;

import com.ford.turbo.aposb.common.authsupport.CredentialsSourceHelper;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VCAPServicesTest {

    @Test
    public void Should_loadVcapWithName() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
        assertThat(credentialsSource).isNotNull();
    }
}