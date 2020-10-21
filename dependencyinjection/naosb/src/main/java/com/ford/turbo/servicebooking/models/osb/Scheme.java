package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class Scheme {
	private BigDecimal leadTime;
	private BigDecimal timeSlotDuration;
	private String lastUpdateUser;
	private String lastUpdateTime;
	private List<ServiceSettings> serviceSettings = new ArrayList<ServiceSettings>();
	private String createUser;
	private String createTime;
	@JsonProperty("_type")
	private String type;
	private BigDecimal id;
}
