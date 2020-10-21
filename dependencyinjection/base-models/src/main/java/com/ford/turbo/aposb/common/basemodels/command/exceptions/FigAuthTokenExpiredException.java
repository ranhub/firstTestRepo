package com.ford.turbo.aposb.common.basemodels.command.exceptions;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class FigAuthTokenExpiredException extends HystrixBadRequestException implements HasFordError {
	
	private static final long serialVersionUID = 3053573195146400599L;

	private static final String MESSAGE = "Auth token is expired";
	
	private FordError FORD_ERROR;

    public FigAuthTokenExpiredException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(StatusContext.FIG.getStatusContext(), FigAuthResponseStatus.EXPIRED, MESSAGE, this.getClass());
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
