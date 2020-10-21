package com.ford.turbo.servicebooking.command;

import static org.springframework.http.HttpMethod.GET;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.osb.response.dealerdetails.DealerDetailsResponse;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.utils.APOsbResponseHelper;

public class LoadOsbSpecificDealerDetailsCommand extends TimedHystrixCommand<DealerDetailsResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(LoadOsbSpecificDealerDetailsCommand.class);
    private static final String API_URL = "/rest/c/b/r/se?p=%s&jk=%s";

    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private final String baseUrl;

    private final List<String> dealerCodes;
    private final String countryCode;

    public LoadOsbSpecificDealerDetailsCommand(TraceInfo traceInfo,
                                               @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                               @NotNull String baseUrl,
                                               @NotNull List<String> dealerCodes,
                                               @NotNull String countryCode){
        super(traceInfo, "LoadOsbSpecificDealerDetailsCommand");
        this.mutualAuthRestTemplate = mutualAuthRestTemplate;
        this.dealerCodes = dealerCodes;
        this.countryCode = countryCode;
        this.baseUrl = baseUrl;
    }

    @Override
    public DealerDetailsResponse doRun() throws Exception {
        HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<Void> requestEntity = new HttpEntity<Void>(headers);
        
        ResponseEntity<String> responseEntity = mutualAuthRestTemplate.exchange(buildURL(), GET, requestEntity, String.class);
        
        String body = responseEntity.getBody();
        LOG.debug("Response body: {}", body);

        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        DealerDetailsResponse[] responses = objectMapper.readValue(responseEntity.getBody(), DealerDetailsResponse[].class);

        final DealerDetailsResponse response = APOsbResponseHelper.fixOsbResponse(responses, DealerDetailsResponse.class);

        return response;
    }
    
	public String buildURL() {
		String formattedDealerCodes = StringUtils.join(dealerCodes, ",");
		String oSBURL = baseUrl + String.format(API_URL, formattedDealerCodes, countryCode);
		return oSBURL;
	}
}
