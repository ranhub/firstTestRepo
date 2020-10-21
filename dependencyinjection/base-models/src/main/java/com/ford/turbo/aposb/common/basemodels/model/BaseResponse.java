package com.ford.turbo.aposb.common.basemodels.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(value = "success")
public class BaseResponse {

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final String LOCAL_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LOCAL_DATE_FORMAT_PATTERN = "yyyy-MM-dd";


    public enum RequestStatus {
        CURRENT, CACHED, UNAVAILABLE
    }

    @ApiModelProperty(required = true)
    private RequestStatus requestStatus;

    private FordError error;

    @JsonFormat(pattern=DATE_TIME_FORMAT_PATTERN)
    @ApiModelProperty(required = true, example = "2009-02-12T00:00:00.1234",dataType = "java.lang.String")
    private ZonedDateTime lastRequested;

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public ZonedDateTime getLastRequested() {
        return lastRequested;
    }

    public void setLastRequested(ZonedDateTime lastRequested) {
        this.lastRequested = lastRequested;
    }

    public FordError getError() {
        return error;
    }

    public void setError(FordError error) {
        this.error = error;
    }
}
