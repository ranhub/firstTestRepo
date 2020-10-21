package com.ford.turbo.aposb.common.basemodels.utility;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import org.springframework.http.HttpStatus;

public class HasFordErrorException extends Exception implements HasFordError {
    private final FordError fordError;
    private final HttpStatus httpStatus;

    public HasFordErrorException(FordError fordError, HttpStatus httpStatus) {
        this.fordError = fordError;
        this.httpStatus = httpStatus;
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
            return httpStatus;
        }
}
