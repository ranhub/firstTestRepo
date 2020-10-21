package com.ford.turbo.servicebooking.models.osb;

import java.util.ArrayList;
import java.util.List;

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
public class OSBCCalendarLoadDealerDetailsData   {
  private String unAvailableBookingForTimeBoxedServices;
  private String unavailableDates;
  private Scheme scheme;
  @JsonProperty("bookingDates")
  private List<TimeAsDate> bookedDates = new ArrayList<TimeAsDate>();
  private List<TimeAsDate> unavailableTimeSlots = new ArrayList<TimeAsDate>();
  private List<TimeAsDate> exceptionDates = new ArrayList<TimeAsDate>();
  private String exceptions;
}
