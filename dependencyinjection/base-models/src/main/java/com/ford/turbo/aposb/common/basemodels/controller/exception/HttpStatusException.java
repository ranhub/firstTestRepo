package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class HttpStatusException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private final HttpStatus httpStatus;
    private final FordError fordError;

    public HttpStatusException(HttpStatus httpStatus, Exception e) {
        super(httpStatus.getReasonPhrase(), e);
        this.httpStatus = httpStatus;
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), httpStatus.value(), httpStatus.getReasonPhrase(), e.getClass());
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
