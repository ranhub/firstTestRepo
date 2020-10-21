package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class GatewayTimeoutException extends HystrixRuntimeException implements HasFordError {

	private static final long serialVersionUID = 1L;
	
	private final FordError fordError;

    public GatewayTimeoutException(HystrixInvokable<?> hystrixInvokable, Exception cause) {
        super(HystrixRuntimeException.FailureType.TIMEOUT, hystrixInvokable.getClass(), HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(), cause, null);
        this.fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.GATEWAY_TIMEOUT.value(), hystrixInvokable.getClass().getSimpleName() + " timed out");
    }

    @Override
    public FordError getFordError() {
        return fordError;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.GATEWAY_TIMEOUT;
    }
}
