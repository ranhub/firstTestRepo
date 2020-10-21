package com.ford.turbo.servicebooking.models.osb.response.dealerdetails;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerDetailsData {

    private Boolean osbEnabled;
    private String dealerCode;
    private String marketCode;
    private Boolean osbovEnabled;

    public DealerDetailsData(){}

    @JsonProperty("osbEnabled")
    public boolean isOsbEnabled() {
        return osbEnabled;
    }

    @JsonProperty("osbEnabled")
    public void setOsbEnabled(Boolean osbEnabled) {
        this.osbEnabled = osbEnabled;
    }

    @JsonProperty("dealerCode")
    public String getDealerCode() {
        return dealerCode;
    }

    @JsonProperty("dealerCode")
    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    @JsonProperty("marketCode")
    public String getMarketCode() {
        return marketCode;
    }

    @JsonProperty("marketCode")
    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    @JsonProperty("osbovEnabled")
    public boolean isOsbovEnabled() {
        return osbovEnabled;
    }

    @JsonProperty("osbovEnabled")
    public void setOsbovEnabled(Boolean osbovEnabled) {
        this.osbovEnabled = osbovEnabled;
    }
}
