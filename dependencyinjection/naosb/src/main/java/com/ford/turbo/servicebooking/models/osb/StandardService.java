package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardService {
	private StandardServiceServiceLabour serviceLabour;
	private String applicationInformation;
	private String createUser;
	@JsonProperty("_type")
	private String type;
	private ServiceFluid serviceFluids;
	private BigDecimal id;
	private String serviceDescription;
	private String fixedPrices;
	private String lastUpdateUser;
	private OSBCEcatMainServiceServicePart servicePart;
	private BigDecimal ecatPartsPriceTotal;
	private String lastUpdateTime;
	private String mainServiceCode;
	private BigDecimal ecatLabourTimeTotal;
	private String createTime;
}
