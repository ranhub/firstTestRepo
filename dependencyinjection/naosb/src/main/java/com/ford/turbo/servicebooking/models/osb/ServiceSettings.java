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
public class ServiceSettings {
	private BigDecimal dayOfWeek;
	private BigDecimal workingFrom;
	private String workingToStr;
	private BigDecimal collectionDeadline;
	private BigDecimal breakTo;
	private String createUser;
	@JsonProperty("_type")
	private String type;
	private BigDecimal maxBookingsPerSlot;
	private BigDecimal id;
	private String workingFromStr;
	private String breakToStr;
	private BigDecimal weeklySchemeId;
	private String breakFromStr;
	private String lastUpdateUser;
	private BigDecimal workingTo;
	private BigDecimal maxBookingsPerDay;
	private String lastUpdateTime;
	private BigDecimal maxLoanCars;
	private BigDecimal breakFrom;
	private String createTime;
}
