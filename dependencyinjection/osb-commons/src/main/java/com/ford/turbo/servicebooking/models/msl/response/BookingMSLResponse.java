package com.ford.turbo.servicebooking.models.msl.response;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

public class BookingMSLResponse extends BaseResponse {

    private String bookingCustomerRefNum;

    public String getBookingCustomerRefNum() {
        return bookingCustomerRefNum;
    }

    public void setBookingCustomerRefNum(String bookingCustomerRefNumber) {
        this.bookingCustomerRefNum = bookingCustomerRefNumber;
    }
}
