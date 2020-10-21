package com.ford.turbo.servicebooking.models.eu.web;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerDetails {
	private DealerProfile dealerProfile;
	private List<OldServicesWeb> oldServices;
	private String dealerCode;
	private List<AdditionalServicesWeb> additionalServices;
	private List<MainServicesWeb> mainServices;
	private String marketCode;
}
