package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OSBOVService {

    private String dealerCode;
    private String price;
    private String model;
    private String marketCode;
    private String name;
    private String uniqueId;
    private String locale;
    private String description;
    private OldServiceType serviceType;
    private SelectedVehicleWithPrice selectedVehicle;
    private String discountPrice;
    private String discountPercentage;
}
