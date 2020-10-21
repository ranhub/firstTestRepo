package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class BadRequestException extends HystrixBadRequestException implements HasFordError {
	
	private static final long serialVersionUID = 1L;
	
	private final FordError fordError;
    private final HttpStatus httpStatus;

    public BadRequestException(Exception cause) {
        super(cause.getMessage(), cause);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(), cause.getMessage(), cause.getClass());
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    public BadRequestException(String message, String context) {
        super(message);
        this.fordError = new FordError(context, HttpStatus.BAD_REQUEST.value(), message, this.getClass());
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BadRequestException(Exception cause, HttpStatus httpStatus) {
        super(cause.getMessage(), cause);
        String message = cause.getMessage();
        if (cause instanceof HttpClientErrorException) {
        	 HttpClientErrorException httpclientCause = (HttpClientErrorException) cause;
        	 message = StringUtils.isNotBlank(httpclientCause.getResponseBodyAsString())? httpclientCause.getResponseBodyAsString() : message ; 	
        }
       
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), httpStatus.value(), message);
        
        if(cause!=null){
        	this.fordError.setSourceException(cause.getClass());
        }
        
        this.httpStatus = httpStatus;
    }
    
    public BadRequestException(Exception cause, String context) {
        super(cause.getMessage(), cause);
        this.fordError = new FordError(context, HttpStatus.BAD_REQUEST.value(), cause.getMessage(), cause.getClass());
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BadRequestException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), httpStatus.value(), httpStatus.getReasonPhrase(), this.getClass());
        this.httpStatus = httpStatus;
    }

    public BadRequestException(FordError error) {
    	super(error.getMessage());
    	this.fordError = error;
    	this.httpStatus = HttpStatus.BAD_REQUEST;
    	if(fordError.getSourceException() == null) {
    		fordError.setSourceException(this.getClass());
    	}
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
