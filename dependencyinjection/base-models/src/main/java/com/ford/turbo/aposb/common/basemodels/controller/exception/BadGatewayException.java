package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;

public class BadGatewayException extends RuntimeException implements HasFordError {

	private static final long serialVersionUID = 661179713874703095L;
	
	private final FordError fordError;

    public BadGatewayException(Exception e) {
        super(e);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_GATEWAY.value(), e.getMessage(), this.getClass());
    }

    public BadGatewayException(String message) {
        super(message);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_GATEWAY.value(), message, this.getClass());
    }

    public BadGatewayException(String message, Exception e) {
        super(message, e);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_GATEWAY.value(), message);
        if(e != null )
        	this.fordError.setSourceException(e.getClass());
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}
