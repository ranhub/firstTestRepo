package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class AuthTokenNotAuthorizedException extends HystrixBadRequestException implements HasFordError {

	private static final long serialVersionUID = -7313510232338655777L;
	
	private final FordError fordError;

    public AuthTokenNotAuthorizedException(String message, Exception e) {
        super(message, e);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.FORBIDDEN.value(), message, this.getClass());
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
