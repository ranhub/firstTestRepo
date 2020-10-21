package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUOSBMainServiceResponse {
	private String serviceId;
	private BigDecimal priceAfterDiscount;
	private BigDecimal discountPrice;
	private BigDecimal price;
	private Long discountPercentage;
	private String subType;
	private String name;
    private String description;
    private String applicationInformation;
}
