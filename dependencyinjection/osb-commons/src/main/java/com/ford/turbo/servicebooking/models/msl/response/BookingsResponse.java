package com.ford.turbo.servicebooking.models.msl.response;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import io.swagger.annotations.ApiModelProperty;

public class BookingsResponse extends BaseResponse {

    private static final Logger LOG = LoggerFactory.getLogger(BookingsResponse.class);

    @ApiModelProperty(required = true)
    private BookedServiceResponse value;

    public static BookingsResponse success(BookedServiceResponse value, ZonedDateTime lastRequestedDate, boolean isCached) {
        BookingsResponse response = new BookingsResponse();

        if(isCached) {
            response.setRequestStatus(RequestStatus.CACHED);
        }else{
            response.setRequestStatus(RequestStatus.CURRENT);
        }

        response.setLastRequested(lastRequestedDate);
        response.value = value;
        return response;
    }

    public BookedServiceResponse getValue() {
        return value;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOG.warn(e.getMessage(), e);
            return super.toString();
        }
    }
}