package com.ford.turbo.aposb.common.authsupport.userprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ford.turbo.aposb.common.basemodels.model.CSDNError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonSDNUserProfileResponse {

	@JsonProperty("version")
	private String version;
	@JsonProperty("profile")
	private CSDNUserProfile profile;
	@JsonProperty("status")
	private Integer status;
	@JsonProperty("error")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private CSDNError error;
}
