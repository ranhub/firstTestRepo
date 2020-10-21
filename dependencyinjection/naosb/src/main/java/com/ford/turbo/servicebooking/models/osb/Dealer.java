package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;

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
public class Dealer {
	private String marketCode;
	private String dealerGroups;
	private String latitude;
	private String longditude;
	private String dealerCode;
	private WeeklyScheme weeklyScheme;
	private String dealerAddlServicesList;
	private String createUser;
	private String exceptions;
	private String type;
	private String lastUpdateUser;
	private String locale;
	private BigDecimal lastUpdateTime;
	private String jobRoles;
	private DealerProfile dealerProfile;
	private String createTime;
}
