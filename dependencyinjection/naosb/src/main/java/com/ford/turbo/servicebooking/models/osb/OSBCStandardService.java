package com.ford.turbo.servicebooking.models.osb;

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
public class OSBCStandardService {
	private String serviceDescription;
	private String applicationInformation;
	private String mainServiceCode;
}
