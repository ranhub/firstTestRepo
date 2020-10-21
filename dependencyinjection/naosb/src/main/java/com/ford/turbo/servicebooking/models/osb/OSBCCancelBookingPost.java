package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OSBCCancelBookingPost {
    private String bookingCustomerRefNum;
    @JsonProperty("OSBSiteTermsRequired")
    private boolean osbSiteTermsRequired;
}
