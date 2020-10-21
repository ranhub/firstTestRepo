package com.ford.turbo.servicebooking.models.msl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class EUWebVehicleLookupResponse extends BaseResponse{
	private VehicleDetailsWrapper value;
}
