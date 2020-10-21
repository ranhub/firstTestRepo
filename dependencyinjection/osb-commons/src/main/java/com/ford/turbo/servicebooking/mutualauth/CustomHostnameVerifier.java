package com.ford.turbo.servicebooking.mutualauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class CustomHostnameVerifier implements HostnameVerifier {
    private static final Logger LOG = LoggerFactory.getLogger(CustomHostnameVerifier.class);

    private HostnameVerifier baseHostnameVerifier;
    private String verifiedHostname;

    public CustomHostnameVerifier(String verifiedHostname, HostnameVerifier baseHostnameVerifier) {
        this.verifiedHostname = verifiedHostname;
        this.baseHostnameVerifier = baseHostnameVerifier;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (baseHostnameVerifier.verify(hostname, session)) {
            return true;
        }

        if (null != hostname && hostname.equals(verifiedHostname)) {
            LOG.info("Verified hostname: " + hostname);
            return true;
        }

        return false;
    }
}
