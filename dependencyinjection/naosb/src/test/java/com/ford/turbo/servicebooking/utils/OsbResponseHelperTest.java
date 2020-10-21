package com.ford.turbo.servicebooking.utils;

import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingResponse;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OsbResponseHelperTest {

    @Test
    public void should_fixOsbResponse(){

        int status = 200;

        OSBCCancelBookingData data = new OSBCCancelBookingData();

        OSBCCancelBookingResponse osbcCancelBookingResponseOne = new OSBCCancelBookingResponse();
        osbcCancelBookingResponseOne.setStatus(status);

        OSBCCancelBookingResponse osbcCancelBookingResponseTwo = new OSBCCancelBookingResponse();
        osbcCancelBookingResponseTwo.setData(data);

        OSBCCancelBookingResponse[] responses = new OSBCCancelBookingResponse[2];
        responses[0] = osbcCancelBookingResponseOne;
        responses[1] = osbcCancelBookingResponseTwo;

        final OSBCCancelBookingResponse response = APOsbResponseHelper.fixOsbResponse(responses, OSBCCancelBookingResponse.class);
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getData()).isEqualTo(data);
    }

}