package com.ford.turbo.aposb.common.basemodels.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =true)
public class VehicleInfo implements Serializable {
    private String vin;
    private String paintDescription;
    private String productTypeVehicleLineCode;
    private String commonName ;
    private String vehicleLineFeatureCode;
    private Integer modelYear;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate saleDate;
    private String productType;
    private String transmissionDescription;
    private String bodyStyleDescription;
    private String driveDescription;
    private String engineFeatureCode;
    private String engineDescription;
    private String fuelType;
    private String brandCode;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime warrantyStartDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime warrantyEndDate;
    private String jointVenture;
    private String masterId;
    private String bodyStyle;
    private String version;
    private String marketName;
    private String buildDate;
    private String emissions;
    private String vehicleLineDescription;
    private String versionDescription;
    private String emissionRequirementPfcCode;
    private String emissionRequirementPfcDescription;
    private String transmission;
    private String driveCode;
    private String emissionRequirementMfcCode;
    private String emissionRequirementMfcDescription;
    private String engineFuelCapabilityCode;
    private String engineFuelCapabilityDescription;
    private String catalyticConverterCode;
    private String catalyticConverterDescription;
    private String dieselParticulateFilterCode;
    private String dieselParticulateFilterDescription;
    private List<AdditionalFeature> additionalFeatures;
    private String dataSource;
    private String transmissionInd;
    private String territory;
    private String territoryDescription;
    private String paint;
}
