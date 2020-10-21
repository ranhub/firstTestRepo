package com.ford.turbo.servicebooking.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class InvalidBookingNumberException extends RuntimeException implements HasFordError {

    private static final FordError FORD_ERROR = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(), "Invalid booking number");

    @Override
    public FordError getFordError() {
        return FORD_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
