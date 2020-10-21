package com.ford.turbo.servicebooking.exception;

import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class InvalidServiceIdException extends BadRequestException {
    private final String message;

    public InvalidServiceIdException(String message) {
        super(new Exception(message));
        this.message = message;
    }

    @Override
    public FordError getFordError() {
        return new FordError(StatusContext.HTTP.getStatusContext(), getHttpStatus().value(), message);
    }
}
