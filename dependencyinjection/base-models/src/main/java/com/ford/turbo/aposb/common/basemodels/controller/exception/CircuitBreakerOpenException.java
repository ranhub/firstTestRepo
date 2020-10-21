package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class CircuitBreakerOpenException extends HystrixRuntimeException implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = " failed because a backend service currently unavailable (circuit breaker open)";
	
	private final FordError fordError;

    public CircuitBreakerOpenException(HystrixInvokable hystrixInvokable, Exception cause) {
        super(FailureType.SHORTCIRCUIT, hystrixInvokable.getClass(), HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), cause, null);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.SERVICE_UNAVAILABLE.value(), hystrixInvokable.getClass().getSimpleName() + MESSAGE, this.getClass());
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

}
