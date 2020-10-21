package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDetails {
    private String engine;
    private Object registrationNumber;
    private String color;
    private String transmission;
    private String vehicleLineCode;
    private Integer mileageInMiles;
    private String bodyStyle;
    private String fuelType;
    private Integer mileageInKm;
    private String modelName;
    private String version;
    private String vin;
    private String buildDate;
    private Object transmissionType;
}