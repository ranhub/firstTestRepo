package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainServicesResponse extends OSBBaseResponse<MainServicesInnerResponse> {

}


