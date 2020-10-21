package com.ford.turbo.aposb.common.basemodels.command.exceptions;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class FigAuthTokenFailedException extends HystrixBadRequestException implements HasFordError {

	private static final String MESSAGE = "Auth token validation failed";

	private static final long serialVersionUID = 4482307451940990511L;
	
	private FordError FORD_ERROR;

    public FigAuthTokenFailedException() {
        super(MESSAGE);
        FORD_ERROR = new FordError(StatusContext.FIG.getStatusContext(), FigAuthResponseStatus.FAILED, MESSAGE, this.getClass());
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
