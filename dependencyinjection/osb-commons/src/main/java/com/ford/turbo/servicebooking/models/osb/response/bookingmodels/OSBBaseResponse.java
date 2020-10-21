package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class OSBBaseResponse <T> {

    private T data;
    private Integer status;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
