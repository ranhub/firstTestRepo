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
public class MainServicesWeb {
	private String serviceId;
	@ApiModelProperty(example = "10.85")
	private BigDecimal priceAfterDiscount;
	@ApiModelProperty(example = "10.85")
	private BigDecimal discountPrice;
	@ApiModelProperty(example = "10.85")
	private BigDecimal price;
    @ApiModelProperty(example = "10")
    private Long discountPercentage;
	private String subType; //Enum
	private String name;
    private String description;
    private String applicationInformation;
}
