package com.ford.turbo.servicebooking.utils;

import java.util.Arrays;

import com.ford.turbo.aposb.common.basemodels.controller.exception.BadGatewayException;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

public class OsbResponseHelper {

    public static <T extends OSBBaseResponse> T fixOsbResponse(T[] responses, Class<T> clazz) {
        if (responses.length == 0) {
            throw new BadGatewayException("A backend service returned no response body");
        }
        try {
            return Arrays.stream(responses)
                    .reduce(clazz.newInstance(), (partial, next) -> {
                        if (next.getData() != null) {
                            partial.setData(next.getData());
                        }

                        if (next.getStatus() != null) {
                            partial.setStatus(next.getStatus());
                        }
                        return partial;
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
