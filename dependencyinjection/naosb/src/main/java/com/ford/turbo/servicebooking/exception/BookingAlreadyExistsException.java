package com.ford.turbo.servicebooking.exception;

import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class BookingAlreadyExistsException extends BadRequestException {

    private static final String ERROR = "Booking exists for this VIN: ";
    private final String message;

    public BookingAlreadyExistsException(String vin) {
        super(new Exception(ERROR + vin));
        this.message = ERROR + vin;
    }

    @Override
    public FordError getFordError() {
        return new FordError("msl-createBooking-OSB", 1001, message);
    }
}
