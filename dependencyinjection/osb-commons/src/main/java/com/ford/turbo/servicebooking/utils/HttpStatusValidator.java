package com.ford.turbo.servicebooking.utils;

import org.springframework.http.HttpStatus;

import com.ford.turbo.aposb.common.basemodels.controller.exception.BadGatewayException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.netflix.hystrix.HystrixCommand;

public class HttpStatusValidator {

    public static void validate(HystrixCommand command, HttpStatus httpStatus) {
        if (httpStatus.is5xxServerError()) {
            throw new BadGatewayException(command.getClass().getSimpleName() + " failed because a backend service returned an HTTP " + httpStatus.value());
        } else if (httpStatus.is4xxClientError()) {
            throw new BadRequestException(httpStatus);
        }
    }
}
