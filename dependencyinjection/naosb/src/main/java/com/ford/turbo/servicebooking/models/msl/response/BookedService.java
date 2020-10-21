package com.ford.turbo.servicebooking.models.msl.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.BookedAdditionalService;
import com.ford.turbo.servicebooking.models.osb.DealerProfile;
import com.ford.turbo.servicebooking.models.osb.OSBBookingData;
import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookedService {
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @JsonFormat(pattern=DATE_TIME_FORMAT_PATTERN)
    private ZonedDateTime appointmentTimeAsDate;

    private DealerProfile dealerProfile;

    private String mainServiceDescription;

    private String mainServicePrice;

    private List<BookedAdditionalService> bookedAdditionalServices;

    private String customerAnnotation;

    private double total;

    private String bookingCustomerRefNum;

    public BookedService() {
        // needed for jackson
    }

    public BookedService(OSBBookingData data){

        ZonedDateTime zonedDateTime = convertAppointmentToZonedDate(data.getAppointmentTimeAsDate());
        this.setAppointmentTimeAsDate(zonedDateTime);
        this.setDealerProfile(data.getDealer().getDealerProfile());
        this.setMainServiceDescription(data.getMainServiceDescription());
        this.setMainServicePrice(data.getMainServicePrice());
        this.setBookedAdditionalServices(data.getBookedAdditionalServices());
        this.setCustomerAnnotation(data.getCustomerAnnotation());
        this.setBookingCustomerRefNum(data.getBookingCustomerRefNum());
    }

    private ZonedDateTime convertAppointmentToZonedDate(TimeAsDate d){

        ZoneOffset offsetInHours = ZoneOffset.ofHours(d.getTimezoneOffset().intValueExact()/60);
        ZoneId zoneId = ZoneId.ofOffset("UTC", offsetInHours);

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getTime().longValue()), zoneId);

        return zonedDateTime;
    }

    public ZonedDateTime getAppointmentTimeAsDate() {
        return appointmentTimeAsDate;
    }

    public void setAppointmentTimeAsDate(ZonedDateTime appointmentTimeAsDate) {

        this.appointmentTimeAsDate = appointmentTimeAsDate;
    }

    public DealerProfile getDealerProfile() {
        return dealerProfile;
    }

    public void setDealerProfile(DealerProfile dealerProfile) {
        this.dealerProfile = dealerProfile;
    }

    public String getMainServiceDescription() {
        return mainServiceDescription;
    }

    public void setMainServiceDescription(String mainServiceDescription) {
        this.mainServiceDescription = mainServiceDescription;
    }

    public String getMainServicePrice() {
        return mainServicePrice;
    }

    public void setMainServicePrice(String mainServicePrice) {
        this.mainServicePrice = mainServicePrice;
    }

    public List<BookedAdditionalService> getBookedAdditionalServices() {
        return bookedAdditionalServices;
    }

    public void setBookedAdditionalServices(List<BookedAdditionalService> bookedAdditionalServices) {
        this.bookedAdditionalServices = bookedAdditionalServices;
    }

    public String getCustomerAnnotation() {
        return customerAnnotation;
    }

    public void setCustomerAnnotation(String customerAnnotation) {
        this.customerAnnotation = customerAnnotation;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getBookingCustomerRefNum() {
        return bookingCustomerRefNum;
    }

    public void setBookingCustomerRefNum(String bookingCustomerRefNum) {
        this.bookingCustomerRefNum = bookingCustomerRefNum;
    }

  /*
        appointmentTimeAsDate: time of the booked service (note: object is a java.util.Date serialized format; need to deserialize and output in UTC ISO date format)
        dealer.dealerProfile.[dealerCode|dealerName|street|town|postalCode|phone]: dealer info of the booked service
        mainServiceDescription|mainServicePrice: for main service name & price
        bookedAdditionalServices[#].[additionalServiceName|price]: for additional service name(s) & price(s) - additional services are optional
        customerAnnotation: for customer comment
        total: work-in-progress for OSB; will provide total of main+additional service prices
        bookingCustomerRefNum: this will be used for cancellation purposes (different story)
        AS MENTIONED ABOVE, SKIP RECORDS: WHERE "mainServiceId"=0 OR "vin"<>[VIN passed in - if applicable]
      */
}
