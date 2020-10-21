package com.ford.turbo.servicebooking.command;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.HttpStatusValidator;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.request.dealercalendar.OSBDealerCalendarRequest;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.OSBDealerCalendarResponse;
import com.ford.turbo.servicebooking.utils.APOsbResponseHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EUDealerCalendarCommand extends TimedHystrixCommand<OSBDealerCalendarResponse> {

	private static final String API_URL = "/rest/c/c/r/gh";

	private final MutualAuthRestTemplate mutualAuthRestTemplate;
	private final String baseUrl;
	private final OSBDealerCalendarRequest request;

	public EUDealerCalendarCommand(TraceInfo traceInfo, @NotNull MutualAuthRestTemplate mutualAuthRestTemplate,
                                   @NotNull String baseUrl, @NotNull OSBDealerCalendarRequest request) {
		super(traceInfo, "EUDealerCalendarCommand");
		this.mutualAuthRestTemplate = Objects.requireNonNull(mutualAuthRestTemplate);
		this.baseUrl = baseUrl;
		this.request = request;
	}

	@Override
	public OSBDealerCalendarResponse doRun() throws Exception {
		URIBuilder builder = new URIBuilder(baseUrl + API_URL);

		HttpHeaders headers = new HttpHeaders();
		Utilities.populateRequestTraceForCommand(headers, this);
		HttpEntity<OSBDealerCalendarRequest> requestEntity = new HttpEntity<OSBDealerCalendarRequest>(request, headers);

		ResponseEntity<String> responseEntity = mutualAuthRestTemplate.postForEntity(builder.build().toString(),
				requestEntity, String.class);
		String body = responseEntity.getBody();

		log.debug("Response body: {}", body);

		OSBDealerCalendarResponse[] osbDealerCalendarResponses = new ObjectMapper(new JsonFactory()).readValue(body,
				OSBDealerCalendarResponse[].class);

		final OSBDealerCalendarResponse response = APOsbResponseHelper.fixOsbResponse(osbDealerCalendarResponses,
				OSBDealerCalendarResponse.class);
		HttpStatusValidator.validate(this, HttpStatus.valueOf(response.getStatus()));
		return response;
	}
}