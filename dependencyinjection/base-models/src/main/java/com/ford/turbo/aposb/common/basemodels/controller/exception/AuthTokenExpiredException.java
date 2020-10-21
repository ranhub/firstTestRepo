package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class AuthTokenExpiredException extends HystrixBadRequestException implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = "Expired Auth Token";
	
	private FordError FORD_ERROR;

    public AuthTokenExpiredException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(StatusContext.NGSDN.getStatusContext(), HttpStatus.FORBIDDEN.value(), MESSAGE, this.getClass());
    }

    @Override
    public FordError getFordError() {
        return FORD_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
