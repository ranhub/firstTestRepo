package com.ford.turbo.servicebooking.models.msl.request;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    private CountryCode country;
    private LanguageCode language;
    private RegionCode region;
    private String vin;
    private String mileage;
    private String dealerCode;
    private String mainServiceId;
    private List<AdditionalServiceRequest> additionalServices;
    private List<String> oldServices;
    private String voucherCode;
    private String customerAnnotation;
    @JsonFormat(pattern= BaseResponse.DATE_TIME_FORMAT_PATTERN)
    @ApiModelProperty(
            required = true,
            example = "yyyy-MM-dd\'T\'HH:mm:ss.SSSX",
            dataType = "java.lang.String"
    )
    private ZonedDateTime apptTime;
    
    private String serviceAdvisorId;
    private Customer customer;
    private String comments;
    private Dealer dealer;
    @ApiModelProperty(notes="Only applicable for AP")
    private LoanerCar loanerCar;
    @ApiModelProperty(notes="Only applicable for AP")
    private String licensePlate;
}
