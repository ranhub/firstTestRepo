package com.ford.turbo.servicebooking.models.msl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OSBOVService {

    private String price;
    private String name;
    private String serviceId;
    private String description;
    private String priceAfterDiscount;
    private OldServiceType subType;
    private String discountPrice;
    private String discountPercentage;

}
