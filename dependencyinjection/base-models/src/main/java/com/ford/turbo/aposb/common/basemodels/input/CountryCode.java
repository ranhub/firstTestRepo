package com.ford.turbo.aposb.common.basemodels.input;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CountryCode {
    CAN("CAN"),
    USA("USA"),
    GBR("GBR"),
    FRA("FRA"),
    DEU("DEU"),
    CHN("CHN"),
    BRA("BRA"),
    ITA("ITA"),
    ESP("ESP"),
    AUT("AUT"),
    BEL("BEL"),
    DNK("DNK"),
    FIN("FIN"),
    IRL("IRL"),
    NLD("NLD"),
    NOR("NOR"),
    PRT("PRT"),
    POL("POL"),
    SWE("SWE"),
    GRC("GRC"),
    ROM("ROM"),
    HUN("HUN"),
    CZE("CZE"),    
	CHE("CHE"),
	MEX("MEX"),
	LUX("LUX"),
	ROU("ROU"),
	PRI("PRI"),
	VIR("VIR");

	
    String jsonName;

    CountryCode(String jsonName) {
        this.jsonName = jsonName;
    }

    @JsonValue
    public String getJsonName() {
        return jsonName;
    }
}
