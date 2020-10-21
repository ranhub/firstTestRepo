package com.ford.turbo.servicebooking.models.msl.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VinLookupResponse {

    @ApiModelProperty(required = true)
    private String requestStatus;

    @JsonFormat(
            pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSSX"
    )
    @ApiModelProperty(
            required = true,
            example = "2009-02-12T00:00:00.1234",
            dataType = "java.lang.String"
    )
    private ZonedDateTime lastRequested;

    @ApiModelProperty(required = false)
    private FordError error;

    @ApiModelProperty(required = true)
    private VinLookupDetailsResponse value;
}