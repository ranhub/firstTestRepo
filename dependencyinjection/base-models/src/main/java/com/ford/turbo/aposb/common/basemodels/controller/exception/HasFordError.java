package com.ford.turbo.aposb.common.basemodels.controller.exception;

import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import org.springframework.http.HttpStatus;

public interface HasFordError {
    FordError getFordError();
    HttpStatus getHttpStatus();

    static HasFordError extractFrom(Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof HasFordError){
                return (HasFordError) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }

    static FordError extractFromWithFallback(Throwable t) {
        if (extractFrom(t) == null) {
            FordError fe = new FordError(StatusContext.HTTP.getStatusContext(), 500, t.getMessage());
            return fe;
        }
        return extractFrom(t).getFordError();
    }
}
