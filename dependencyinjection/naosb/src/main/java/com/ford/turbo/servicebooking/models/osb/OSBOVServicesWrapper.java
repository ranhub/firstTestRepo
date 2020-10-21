package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OSBOVServicesWrapper {
	OSBOVService [] motJSON;
	OSBOVService [] valueServiceJSON;
	OSBOVService [] repairsJSON;
	String error;
	int code;
}
