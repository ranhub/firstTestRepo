package com.ford.turbo.servicebooking.models.msl.response.v2;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ford.turbo.servicebooking.models.msl.response.AdditionalServiceBooking;
import com.ford.turbo.servicebooking.models.msl.response.MainServiceBooking;
import com.ford.turbo.servicebooking.models.msl.response.OldServiceBooking;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BookingDetailsServiceResponseValue {

	private static final int OLD_MAINSERVICE_ID = 0;
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @JsonFormat(pattern=DATE_TIME_FORMAT_PATTERN)
    @ApiModelProperty(
            required = true,
            example = "2009-02-12T00:00:00.1234",
            dataType = "java.lang.String"
    )
    private ZonedDateTime appointmentTimeAsDate;

    private String bookingCustomerRefNum;

    private int totalBookedServices; //Default for EU = 0;

    private String customerAnnotation;

    List<MainServiceBooking> mainServices = new ArrayList<>();

    List<AdditionalServiceBooking> additionalServices = new ArrayList<>();
    
    List<OldServiceBooking> oldServices = new ArrayList<>();
    
    private String totalPriceAfterDiscount;
    private String totalPrice;
    
    private BookingAppointment bookingAppointment;
	private Dealer dealer;
	private Customer customer;
	private String serviceAdvisorName;
	private String comments;
	private String mileage;
	private String mainServiceId;
	@ApiModelProperty(notes = "Only applicable for AP")
	private String loanerCar;
	@ApiModelProperty(notes = "Only applicable for AP")
	private String licensePlate;
	
}
