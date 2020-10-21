package com.ford.turbo.servicebooking.models.msl.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingWebRequest {
	@Valid
	@NotEmpty(message = "Required parameter 'locale' is missing or empty")
	@ApiModelProperty(example = "en-GB")
	private String locale;
	
	@Valid
	@NotEmpty(message = "Required parameter 'marketCode' is missing or empty")
	@ApiModelProperty(example = "GBR")
	private String marketCode;
	
	@Valid
	@NotEmpty(message = "Required parameter 'dealerCode' is missing or empty")
	private String dealerCode;
	
	private String modelName;
	private String buildYear;
	private String vin;
	private String registrationNumber;
	private List<String> voucherCodes;
	@Valid
	@NotEmpty(message = "Required parameter 'serviceType' is missing or empty")
	private String serviceType;
	
	@Valid
	@NotNull(message = "Missing required parameter 'osbSiteTermsRequired'")
	private Boolean osbSiteTermsRequired;
	
	private String comments;
	
	@ApiModelProperty(example = "25-05-2018T15:30:00")
	@Valid
	@NotEmpty(message = "Required parameter 'appointmentTime' is missing or empty")
	private String appointmentTime;
	
	private String mainServiceId;
	private String valueServiceId;
	private String motServiceId;
	private List<String> repairServices;
	private List<String> additionalServices;
	
	@Valid
	@NotNull(message = "Missing required parameter 'customer'")
	private EUWebCustomer customer;
}
