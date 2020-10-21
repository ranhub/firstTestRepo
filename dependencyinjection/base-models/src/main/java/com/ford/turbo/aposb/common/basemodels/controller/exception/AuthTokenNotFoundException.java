package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class AuthTokenNotFoundException extends HystrixBadRequestException implements HasFordError {

	private static final String MESSAGE = "Authorization has been denied for this request. Token could be missing.";

	private static final long serialVersionUID = 1L;
	
	private FordError FORD_ERROR;

    public AuthTokenNotFoundException() {
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
