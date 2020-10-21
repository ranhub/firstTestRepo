package com.ford.turbo.servicebooking.models.eu.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailsWeb {

	private String engine;
    private String registrationNumber;
    private String color;
    private String transmission;
    private String vehicleLineCode;
    private String mileageInMiles;
    private String bodyStyle;
    private String fuelType;
    private String mileageInKm;
    private String modelName;
    private String version;
    private String vin;
    private String buildDate;
    private String transmissionType;
}
