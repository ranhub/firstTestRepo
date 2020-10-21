package com.ford.turbo.servicebooking.models.msl.request;

public class AdditionalServiceRequest {
    private String additionalServiceId;
    private String additionalServiceComments;

    public AdditionalServiceRequest() {}

    public AdditionalServiceRequest(String additionalServiceId, String additionalServiceComments) {
        this.additionalServiceId = additionalServiceId;
        this.additionalServiceComments = additionalServiceComments;
    }

    public String getAdditionalServiceId() {
        return additionalServiceId;
    }

    public String getAdditionalServiceComments() {
        return additionalServiceComments;
    }
}
