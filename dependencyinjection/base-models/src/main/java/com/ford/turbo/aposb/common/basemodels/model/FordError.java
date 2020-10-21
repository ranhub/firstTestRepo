package com.ford.turbo.aposb.common.basemodels.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

public class FordError implements Serializable {

   	private static final long serialVersionUID = -3469672524093485211L;

   	@JsonIgnore
   	private Class<?> sourceException;
   	
	@ApiModelProperty(required = true)
    private String statusContext;

    @ApiModelProperty(required = true)
    private int statusCode;

    @ApiModelProperty(required = true)
    private String message;

    public FordError() {
    }

    public FordError(String statusContext, int statusCode, String message) {
        this.statusContext = statusContext;
        this.statusCode = statusCode;
        this.message = message;
    }
    
    public FordError(String statusContext, int statusCode, String message, Class<?> sourceException) {
    	this(statusContext, statusCode, message);
    	this.sourceException = sourceException;
    }
    
    public Class<?> getSourceException() {
		return sourceException;
	}

	public void setSourceException(Class<?> sourceException) {
		this.sourceException = sourceException;
	}

	public String getStatusContext() {
        return statusContext;
    }

    public void setStatusContext(String statusContext) {
        this.statusContext = statusContext;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "FordError{" +
                "statusContext=" + statusContext +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
