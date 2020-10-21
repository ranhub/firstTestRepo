package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class NoBackendConfiguredException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = 3926854835724239030L;
	
	private static final int STATUS_CODE = 3002;
    private static final String STATUS_CONTEXT = "Marketing Services Layer";
    private static final String MESSAGE = "Backend configuration is currently not configured for this endpoint";

    private FordError FORD_ERROR;

    public NoBackendConfiguredException() {
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
