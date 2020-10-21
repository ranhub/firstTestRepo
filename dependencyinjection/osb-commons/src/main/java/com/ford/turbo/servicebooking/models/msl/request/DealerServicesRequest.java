package com.ford.turbo.servicebooking.models.msl.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealerServicesRequest {
	private String dealerCode;
	private String  marketCode; 
	private String  locale;
	private String  modelName;
	private String  buildYear; 
	private String  vin;
	private String  registrationNumber; 
	private List<String> voucherCode;
	private String combinedVoucherCodes;
	private String mileage;
}
