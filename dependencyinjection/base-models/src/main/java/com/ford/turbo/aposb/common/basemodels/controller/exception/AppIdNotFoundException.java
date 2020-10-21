package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class AppIdNotFoundException extends HystrixBadRequestException implements HasFordError {
	
	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = "Authorization has been denied for this request. App Id could be missing.";
	
	private FordError FORD_ERROR;

    public AppIdNotFoundException() {
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
