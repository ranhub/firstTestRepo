package com.ford.turbo.aposb.common.basemodels.input;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LanguageCode {
    EN("EN"),
    FR("FR"),
    DE("DE"),
    PT("PT"),
	ZH("ZH"),
	IT("IT"),
	ES("ES"),
	DA("DA"),
	NL("NL"),
	NB("NB"),
	PL("PL"),
	FI("FI"),
	SV("SV"),
	NO("NO"),
	EL("EL"),
	RO("RO"),
	HU("HU"),
	CS("CS"),
	LU("LU");
	
    String jsonName;

    LanguageCode(String jsonName) {
        this.jsonName = jsonName;
    }

    @JsonValue
    public String getJsonName() {
        return jsonName;
    }
}
