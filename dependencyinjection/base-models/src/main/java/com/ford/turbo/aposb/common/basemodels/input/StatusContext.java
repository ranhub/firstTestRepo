package com.ford.turbo.aposb.common.basemodels.input;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusContext {

    HTTP("HTTP"),
    NGSDN("NGSDN"),
    MSL("Marketing Services Layer"),
    FIG("FIG");
	
    String statusContext;

    StatusContext(String statusContext) {
        this.statusContext = statusContext;
    }

    @JsonValue
    public String getStatusContext() {
        return statusContext;
    }

}
