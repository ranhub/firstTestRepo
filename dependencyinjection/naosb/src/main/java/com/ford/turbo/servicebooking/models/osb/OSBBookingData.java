package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;
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
public class OSBBookingData   {
  private String bookingStatus;
  private StandardService standardService;
  private BigDecimal loanCarCounter;
  private String auditTimeStamp;
  private TimeAsDate auditTimeStampAsDate;
  private String sameDayCollectionTime;
  private String appointmentTime;
  private String registrationNumber;
  private String serviceType;
  private String bookingDealerRefNum;
  private String odometer;
  private String bookingCustomerRefNum;
  private String contactByPhone;
  private TimeAsDate appointmentTimeAsDate;
  private String fsaActions;
  private String voucherCodes;
  private BigDecimal bookingID;
  private String secureStoreToken;
  private String marketCode;
  private String customerAccessCode;
  private String customerAnnotation;
  private String dealerCode;
  private String vin;
  private String contactByEmail;
  private List<BookedAdditionalService> bookedAdditionalServices;
  private Dealer dealer;
  private String mainServiceDescription;
  private OSBCLoadBookingCustomer customer;
  private String vehicleLineCode;
  private String customerServiceDescription;
  private Vehicle vehicle;
  private Locale locale;
  private BigDecimal bookingCounter;
  private BigDecimal mainServiceId;
  private String mainServicePrice;
  private TimeAsDate sameDayCollectionTimeAsDate;
  private OSBOVService [] motJSON;
  private OSBOVService [] valueServiceJSON;
  private OSBOVService [] repairsJSON;
  private String totalPriceAfterDiscount;
  private String totalPrice;
}
