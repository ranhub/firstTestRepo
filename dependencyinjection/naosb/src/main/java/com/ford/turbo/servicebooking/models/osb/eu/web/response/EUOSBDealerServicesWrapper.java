package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWeb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EUOSBDealerServicesWrapper {
	private DealerServicesWeb data;
}
