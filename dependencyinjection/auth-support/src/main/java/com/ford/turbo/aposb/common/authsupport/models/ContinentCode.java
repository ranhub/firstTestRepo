package com.ford.turbo.aposb.common.authsupport.models;

public enum ContinentCode {
    NA("NA"),
    EU("EU"),
    AP("AP"),
    SA("SA");

    String continentName;

    ContinentCode(String continentName) {
        this.continentName = continentName;
    }

    public String getContinentName() {
        return continentName;
    }
}
