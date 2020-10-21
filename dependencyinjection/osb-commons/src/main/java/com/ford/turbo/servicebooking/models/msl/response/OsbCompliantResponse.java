package com.ford.turbo.servicebooking.models.msl.response;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

public class OsbCompliantResponse extends BaseResponse {

    private boolean isCompliant;

    public boolean isCompliant() {
        return isCompliant;
    }

    public void setCompliant(boolean compliant) {
        isCompliant = compliant;
    }
}
