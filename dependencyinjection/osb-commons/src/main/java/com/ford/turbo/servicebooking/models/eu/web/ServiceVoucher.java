package com.ford.turbo.servicebooking.models.eu.web;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceVoucher {
	private String description;
	@ApiModelProperty(example = "10.85")
	private BigDecimal amount;
	@ApiModelProperty(example = "10")
	private Long percentage;
	private String code;
}
