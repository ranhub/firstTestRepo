package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class InvalidLanguageCombinationException extends Exception implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private final FordError fordError;

    public InvalidLanguageCombinationException (String reason) {
        super(reason);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(), reason, this.getClass());
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
