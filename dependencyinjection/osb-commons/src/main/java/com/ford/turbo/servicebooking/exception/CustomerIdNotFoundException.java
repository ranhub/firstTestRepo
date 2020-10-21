package com.ford.turbo.servicebooking.exception;

import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class CustomerIdNotFoundException extends BadRequestException {
    private static final String message = "Customer Id not found";

    public CustomerIdNotFoundException() {
        super(new Exception(message));
    }

    @Override
    public FordError getFordError() {
        return new FordError(StatusContext.HTTP.getStatusContext(), getHttpStatus().value(), message);
    }
}
