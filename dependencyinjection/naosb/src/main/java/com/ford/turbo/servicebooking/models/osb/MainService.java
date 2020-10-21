package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;

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
public class MainService   {
  private String mainServiceDescription;
  private String vehicleLineCode;
  private String mainServiceCode;
  private BigDecimal mainServicePrice;
}

