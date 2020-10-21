package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePart_ {
    private String partCode;
    private Double partPrice;
    private String partPriceCurrency;
    private String partDescription;
    private String partQuantity;
    private Map<String, Object> additionalProperties;
}
