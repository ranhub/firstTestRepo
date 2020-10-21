package com.ford.turbo.aposb.common.basemodels.command.exceptions;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class FigAuthTokenRevokedException extends HystrixBadRequestException implements HasFordError {

	private static final String MESSAGE = "Auth token has been revoked";

	private static final long serialVersionUID = -3275836050790892297L;
	
	private FordError FORD_ERROR;

    public FigAuthTokenRevokedException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(StatusContext.FIG.getStatusContext(), FigAuthResponseStatus.REVOKED, MESSAGE, this.getClass());
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
