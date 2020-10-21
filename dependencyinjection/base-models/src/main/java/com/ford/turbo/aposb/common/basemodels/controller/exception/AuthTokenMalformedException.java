package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class AuthTokenMalformedException extends HystrixBadRequestException implements HasFordError {

	private static final String MESSAGE = "Auth token is malformed";

	private static final long serialVersionUID = 1L;
	
	private FordError FORD_ERROR;

    public AuthTokenMalformedException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(), MESSAGE, this.getClass());
    }

    @Override
    public FordError getFordError() {
        return FORD_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
