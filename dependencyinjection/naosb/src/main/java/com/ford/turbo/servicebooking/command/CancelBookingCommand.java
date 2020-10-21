package com.ford.turbo.servicebooking.command;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.HttpStatusValidator;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingPost;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingResponse;
import com.ford.turbo.servicebooking.utils.APOsbResponseHelper;

public class CancelBookingCommand extends TimedHystrixCommand<OSBCCancelBookingResponse> {

    private static final String API_URL = "/rest/s/c/b/bw";

    private static final Logger LOG = LoggerFactory.getLogger(CancelBookingCommand.class);

    private final MutualAuthRestTemplate mutualAuthRestTemplate;
    private final String bookingRefNumber;
    private final String baseUrl;

    public CancelBookingCommand(@NotNull TraceInfo traceInfo,
                                @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                @NotNull String bookingRefNumber,
                                @NotNull String baseUrl) {
        super(traceInfo, "CancelBookingCommand");
        this.mutualAuthRestTemplate = Objects.requireNonNull(mutualAuthRestTemplate);
        this.bookingRefNumber = bookingRefNumber;
        this.baseUrl = baseUrl;
    }

    @Override
    public OSBCCancelBookingResponse doRun() throws Exception {
        String url = new URIBuilder(baseUrl + API_URL).build().toString();
        LOG.debug("Cancel booking request for {}", bookingRefNumber);
        
        HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<OSBCCancelBookingPost> requestEntity = new HttpEntity<OSBCCancelBookingPost>(new OSBCCancelBookingPost(bookingRefNumber, true), headers);

        ResponseEntity<String> responseEntity = mutualAuthRestTemplate.postForEntity(url, requestEntity, String.class);
        String body = responseEntity.getBody();
        LOG.debug("Response body for booking Id {}: {}", bookingRefNumber, body);

        OSBCCancelBookingResponse[] osbcCancelBookingResponses = new ObjectMapper(new JsonFactory()).readValue(body, OSBCCancelBookingResponse[].class);

        final OSBCCancelBookingResponse response = APOsbResponseHelper.fixOsbResponse(osbcCancelBookingResponses, OSBCCancelBookingResponse.class);
        HttpStatusValidator.validate(this, HttpStatus.valueOf(response.getStatus()));
        return response;
    }
}
