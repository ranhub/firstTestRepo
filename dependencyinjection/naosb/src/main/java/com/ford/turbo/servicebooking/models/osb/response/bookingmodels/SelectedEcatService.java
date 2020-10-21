package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import java.util.List;
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
public class SelectedEcatService {
	private List<String> fixedPrices;
	private String applicationInformation;
	private Map<String, Object> servicePart;
	private Map<String, Object> serviceLabour;
}
