package com.ford.turbo.aposb.common.authsupport.models;

public enum Make {
    FORD("F"),
    LINCOLN("L");

    private String make;

    Make(String make) {
        this.make = make;
    }

    public String getMake() {
        return make;
    }
}
