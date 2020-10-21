package com.ford.turbo.aposb.common.authsupport.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdentity {

    @JsonProperty("SdnAppId")
    public String SdnAppId;
    @JsonProperty("ProviderIdentity")
    public String ProviderIdentity;
    @JsonProperty("IdentityProvider")
    public String IdentityProvider;

    public UserIdentity(String providerIdentity) {
        this.ProviderIdentity = providerIdentity;
    }

    public UserIdentity() {
    }

    public String getLightHouseGuid() {
        return ProviderIdentity;
    }
}
