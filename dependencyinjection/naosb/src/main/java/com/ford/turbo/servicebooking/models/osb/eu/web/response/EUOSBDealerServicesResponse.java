package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EUOSBDealerServicesResponse {
	private List<EUOSBOldServiceResponse> oldServices;
	private List<EUOSBAdditionalServiceResponse> additionalServices;
	private List<EUOSBServiceVoucher> voucherCodes;
	private List<EUOSBMainServiceResponse> mainServices;
}
