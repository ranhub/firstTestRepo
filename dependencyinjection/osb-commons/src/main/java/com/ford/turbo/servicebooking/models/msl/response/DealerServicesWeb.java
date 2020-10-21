package com.ford.turbo.servicebooking.models.msl.response;

import java.util.List;

import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealerServicesWeb {
	private List<OldServicesWeb> oldServices;
	private List<AdditionalServicesWeb> additionalServices;
	private List<ServiceVoucher> serviceVouchers;
	private List<MainServicesWeb> mainServices;
}
