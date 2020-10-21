package com.ford.turbo.servicebooking.models.eu.web;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUWebVehicleFeaturesData {
	
	private List<String> model;
	private List<String> modelYear;
	
}