package com.ford.turbo.aposb.common.authsupport.vehicles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ford.turbo.aposb.common.basemodels.model.CSDNError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonSDNUserVehicleResponse {

	@JsonProperty("version")
	private String version;
	@JsonProperty("vehicle")
	private CSDNVehicle vehicle;
	@JsonProperty("status")
	private Integer status;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private CSDNError error;
	
}
