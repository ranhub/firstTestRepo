package com.ford.turbo.aposb.common.authsupport.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FigResponse {

    @JsonProperty("Profile")
    public UserIdentity Profile;

    @JsonProperty("Status")
    public int Status;

}
