package com.ford.turbo.servicebooking.models.msl.response;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookedServiceResponse{

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @JsonFormat(pattern=DATE_TIME_FORMAT_PATTERN)
    @ApiModelProperty(
            required = true,
            example = "2009-02-12T00:00:00.1234",
            dataType = "java.lang.String"
    )
    private ZonedDateTime appointmentTimeAsDate;

    private String bookingCustomerRefNum;

    private DealerProfileMslResponse dealerProfile;

    private int totalBookedServices = 0;

    private String customerAnnotation;

    List<MainServiceBooking> mainServices = new ArrayList<>();

    List<AdditionalServiceBooking> additionalServices = new ArrayList<>();
    
    List<OldServiceBooking> oldServices = new ArrayList<>();
    
    private String totalPriceAfterDiscount;
    
    private String totalPrice;
    
}
