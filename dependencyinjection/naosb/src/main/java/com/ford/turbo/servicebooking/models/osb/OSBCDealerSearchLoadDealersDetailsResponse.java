package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OSBCDealerSearchLoadDealersDetailsResponse extends OSBBaseResponse<OSBCDealerSearchLoadDealersDetailsData[]> {

}

