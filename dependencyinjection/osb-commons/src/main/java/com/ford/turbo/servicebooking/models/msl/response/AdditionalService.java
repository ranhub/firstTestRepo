package com.ford.turbo.servicebooking.models.msl.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalService {

    private String name;
 	private String description;
    private String serviceId;
    private BigDecimal price;
    private BigDecimal priceAfterDiscount;
	private BigDecimal discountPrice;
	private Long discountPercentage;
    private String applicationInformation;

   
    
}
