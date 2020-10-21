package com.ford.turbo.servicebooking.models.osb;

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
public class Fluid   {
  private String fluidQuantity;
  private String fluidCodeDescription;
  private String fluidCode;
  private String marketCode;
}
