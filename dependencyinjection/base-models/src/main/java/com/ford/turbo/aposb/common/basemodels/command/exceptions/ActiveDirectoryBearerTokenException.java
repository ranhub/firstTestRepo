package com.ford.turbo.aposb.common.basemodels.command.exceptions;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class ActiveDirectoryBearerTokenException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = -902639007717581860L;
	
	private FordError FORD_ERROR;

	public ActiveDirectoryBearerTokenException(String message, Throwable cause) {
        super(message, cause);
        FORD_ERROR = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_GATEWAY.value(), getMessage(), this.getClass());
    }

    @Override
    public FordError getFordError() {
        return FORD_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}
