package com.ford.turbo.aposb.common.authsupport.mutualauth;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.HostnameVerifier;

public class HostnameVerifierFactory {
    public static HostnameVerifier createCustomHostnameVerifier(CredentialsSource credentialsSource) {
        String verifiedHostname;
        if (credentialsSource != null) {
            verifiedHostname = credentialsSource.getBaseUri();
        } else {
            verifiedHostname = null;
        }

        return new CustomHostnameVerifier(verifiedHostname, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }
}
