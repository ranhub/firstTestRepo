package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class OSBCDealerSearchLoadDealersDetailsData {
	private String marketCode;
	private List<AdditionalService> additionalServices = new ArrayList<AdditionalService>();
	private MainService mainService;
	private String dealerCode;
	private BigDecimal distance;
	private Boolean selected;
	private DealerProfile dealerProfile;
}
