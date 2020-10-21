package com.ford.turbo.aposb.common.basemodels.input;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RegionCode {
    CA("CA"),
    US("US"),
    UK("UK"),
    FR("FR"),
    DE("DE"),
    BR("BR"),
	CN("CN"),
	IT("IT"),
	ES("ES"),
	AT("AT"),
	BE("BE"),
	DK("DK"),
	FI("FI"),
	GB("GB"),
	NL("NL"),
	NO("NO"),
	PT("PT"),
	PL("PL"),
	SE("SE"),
	IE("IE"),
	RO("RO"),
	GR("GR"),
	HU("HU"),
	CZ("CZ"),
	CH("CH"),
	MX("MX");
	
    String jsonName;

    RegionCode(String jsonName) {
        this.jsonName = jsonName;
    }

    @JsonValue
    public String getJsonName() {
        return jsonName;
    }
}
