package com.ford.turbo.servicebooking.command;

import java.util.Arrays;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.HttpStatusValidator;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ford.turbo.servicebooking.models.osb.RetrieveBookingsForOwnerResponse;
import com.ford.turbo.servicebooking.utils.APOsbResponseHelper;

public class GetBookingsCommand extends TimedHystrixCommand<RetrieveBookingsForOwnerResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(GetBookingsCommand.class);
    private static final String BOOKINGS_PATH = "/rest/s/c/b/wfg?k=%s&r=%s";

    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private final String marketCode;
    private final String baseUrl;
    private String opusConsumerId;

    public GetBookingsCommand(TraceInfo traceInfo,
                              @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                              @NotNull String marketCode,
                              @NotNull String baseUrl,
                              @NotNull String opusConsumerId) {
        super(traceInfo, "GetBookingsCommand");
        this.opusConsumerId = opusConsumerId;
        this.mutualAuthRestTemplate = Objects.requireNonNull(mutualAuthRestTemplate);
        this.marketCode = marketCode;
        this.baseUrl = baseUrl;
    }

    @Override
    public RetrieveBookingsForOwnerResponse doRun() throws Exception {
        String url = baseUrl + String.format(BOOKINGS_PATH, opusConsumerId, marketCode);
        
        HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
        
        ResponseEntity<RetrieveBookingsForOwnerResponse[]> responseEntity = mutualAuthRestTemplate.exchange(url, HttpMethod.GET, requestEntity, RetrieveBookingsForOwnerResponse[].class);
        RetrieveBookingsForOwnerResponse[] retrieveBookingsForOwnerResponses = responseEntity.getBody();
        
        LOG.debug("Response body: " + Arrays.toString(retrieveBookingsForOwnerResponses));
       
        final RetrieveBookingsForOwnerResponse response = APOsbResponseHelper.fixOsbResponse(retrieveBookingsForOwnerResponses, RetrieveBookingsForOwnerResponse.class);
        HttpStatusValidator.validate(this, HttpStatus.valueOf(response.getStatus()));
        return response;
    }
}