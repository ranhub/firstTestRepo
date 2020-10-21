package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelBookedServiceResponse extends BaseResponse {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
