package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {
	private String marketCode;
	private String buildDate;
	private String vin;
	private String transmissionType;
	private String fuelType;
	private String createUser;
	@JsonProperty("_type")
	private String type;
	private String engineType;
	private String versio;
	private String vehicleLineDescription;
	private String transmission;
	private String vehicleLineCode;
	private String lastUpdateUser;
	private String lastUpdateTime;
	private String exteriorPaint;
	private String interiorEnvironment;
	private String licenseplate;
	private String mileage;
	private String createTime;
	private String bodyStyle;
}
