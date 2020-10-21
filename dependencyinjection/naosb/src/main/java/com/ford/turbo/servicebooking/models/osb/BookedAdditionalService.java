package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookedAdditionalService {

	@JsonProperty("additionalServicePrice")
	private BigDecimal price;
	private String createUser;

	@JsonProperty("_type")
	private String type;

	private String currency;
	private String additionalServiceName;
	private String additionalServiceId;
	private String lastUpdateUser;
	private String bookingCustomerRefNum;
	private String lastUpdateTime;
	private String createTime;

	private String additionalServiceComments;
}
