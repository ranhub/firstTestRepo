package com.ford.turbo.aposb.common.basemodels.command.exceptions;

import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class UnknownAppIdException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = -7394204976529843836L;
	
	private FordError fordError;

    public UnknownAppIdException(String appId) {
        super("Unknown application id " + appId);
        fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.FORBIDDEN.value(), getMessage(), this.getClass());
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
