package com.ford.turbo.servicebooking.models.eu.web;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OldServicesWeb {
	private String serviceId;
	@ApiModelProperty(example = "10.85")
	private BigDecimal priceAfterDiscount;
	@ApiModelProperty(example = "10.85")
	private BigDecimal discountPrice;
	@ApiModelProperty(example = "10.85")
	private BigDecimal price;
    private String subType;//think this is ENUM
    @ApiModelProperty(example = "10")
    private Long discountPercentage;
    private String name;
    private String description;
}
