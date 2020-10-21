package com.ford.turbo.aposb.common.authsupport.models;

public class SdnToken {

    private String sdnToken;
    private String sdnAppId;

    public SdnToken(String token, String sdnAppId)  {
        this.sdnToken = token;
        this.sdnAppId = sdnAppId;
    }

    public SdnToken() {
        // for deserialization
    }

    public String getSdnToken() {
        return sdnToken;
    }

    public String getSdnAppId() {
        return sdnAppId;
    }
}
