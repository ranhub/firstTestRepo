package com.ford.turbo.servicebooking.models.osb;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;

public class DealerCalendarV2Response extends BaseResponse {

    private DealerCalendarV2 value;

    public DealerCalendarV2 getValue() {
        return value;
    }

    public void setValue(DealerCalendarV2 value) {
        this.value = value;
    }
}
