package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class InvalidVinException extends HystrixBadRequestException implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private final FordError fordError;

    public InvalidVinException(String... vins) {
        super("VIN (" + StringUtils.join(vins, ", ") + ") must be 17 uppercase alphanumeric characters");
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(), getMessage(), this.getClass());
    }
    
    public InvalidVinException(String vin, int statusCode, String message) {
    	super(message);
    	this.fordError = new FordError("Marketing Services Layer", statusCode, getMessage(), this.getClass());
	}
    
    public InvalidVinException(FordError fordError) {
    	super(fordError.getMessage());
    	if(fordError.getSourceException() == null) {
    		fordError.setSourceException(this.getClass());
    	}
    	this.fordError = fordError;
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