package com.ford.turbo.aposb.common.authsupport.validator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"$id", "$values"})
public class NGSDNVehiclesResponse  {

    public int status;
    public String version;

}
