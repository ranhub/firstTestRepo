package com.ford.turbo.servicebooking.models.msl.request;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OSBSource {
	LW_CON("LW-CON");
	
    private String jsonName;

    OSBSource(String jsonName) {
        this.jsonName = jsonName;
    }

    @JsonValue
    public String getJsonName() {
        return jsonName;
    }
}
