package com.ford.turbo.servicebooking.models.msl.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VinLookupDetailsResponse {
    @ApiModelProperty(required = true)
    private String vin;
    
    @ApiModelProperty(required = true)
    private String paintDescription;
    
    @ApiModelProperty(required = true)
    private String productTypeVehicleLineCode;
    
    @ApiModelProperty(required = true)
    private String commonName;

    @ApiModelProperty(required = true)
    private String vehicleLineFeatureCode;
    
    @ApiModelProperty(required = true)
    private Integer modelYear;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(required = true)
    private LocalDate saleDate;
    
    @ApiModelProperty(required = true)
    private String productType;
    
    @ApiModelProperty(required = true)
    private String transmissionDescription;
    
    @ApiModelProperty(required = true)
    private String bodyStyleDescription;

    @ApiModelProperty(required = true)
    private String bodyStyle;
    
    @ApiModelProperty(required = true)
    private String driveDescription;
    
    @ApiModelProperty(required = true)
    private String engineFeatureCode;
    
    @ApiModelProperty(required = true)
    private String engineDescription;
    
    @ApiModelProperty(required = true)
    private String fuelType;
    
    @ApiModelProperty(required = true)
    private String brandCode;

    @ApiModelProperty(required = true)
    private String jointVenture;

    @ApiModelProperty(required = true)
    private String version;

    @ApiModelProperty(required = true)
    private String buildDate;
}
