package com.ford.turbo.servicebooking.models.osb.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListMainServicesRequest {
    private String vin;
    private String registerNo;
    private String locale;
    private String mileage;
    private String marketCode;
    private String dealerCode;
    private String ecatMarketCode;
    @JsonProperty("OSBSiteTermsRequired")
    private boolean osbSiteTermsRequired;
}
