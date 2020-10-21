package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EUOSBServiceVoucher {
	private String voucherCodeDescription;
	private BigDecimal voucherAmount;
	private Long voucherPercentage;
	private String voucherCode;
}
