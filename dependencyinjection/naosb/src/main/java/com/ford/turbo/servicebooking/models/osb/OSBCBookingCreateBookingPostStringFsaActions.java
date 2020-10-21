package com.ford.turbo.servicebooking.models.osb;

import java.util.List;

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
public class OSBCBookingCreateBookingPostStringFsaActions   {
  private List<FSA> FSA;
}
