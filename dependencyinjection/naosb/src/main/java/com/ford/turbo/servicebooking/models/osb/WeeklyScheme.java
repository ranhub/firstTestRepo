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
public class WeeklyScheme {
	private BigDecimal leadTime;
	private BigDecimal timeSlotDuration;
	private BigDecimal lastUpdateUser;
	private String lastUpdateTime;
	private String serviceSettings;
	private String createUser;
	private BigDecimal createTime;
	@JsonProperty("_type")
	private String type;
	private BigDecimal id;
}
