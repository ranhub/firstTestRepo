package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class NoBackendAvailableException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = 1L;
	private static final int STATUS_CODE = 3001;
    private static final String STATUS_CONTEXT = "Marketing Services Layer";
    private static final String MESSAGE = "Service is not configured for the request Application ID";

    private FordError FORD_ERROR = new FordError(STATUS_CONTEXT, STATUS_CODE, MESSAGE);

    public NoBackendAvailableException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(STATUS_CONTEXT, STATUS_CODE, MESSAGE, this.getClass());
        
    }

    @Override
    public FordError getFordError() {
        return FORD_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.OK;
    }
}
