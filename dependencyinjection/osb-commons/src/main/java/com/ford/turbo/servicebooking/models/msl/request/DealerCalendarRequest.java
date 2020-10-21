package com.ford.turbo.servicebooking.models.msl.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerCalendarRequest {
	
	private String dealerCode;
	private String marketCode; 
	private String locale;
	private String modelName;
	private List<String> additionalService;
	private String motService;
}
