package com.ford.turbo.servicebooking.service.eu.web;

import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerCalendarRequest;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.DealerCalendarV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.eu.web.EUWebDealerCalendarCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerCalendarResponse;

@Service
public class EUWebDealerCalendarService implements DealerCalendarV2Service {
	private static final String EU_OSB_STATUS_CONTEXT = "EU OSB";
	private TraceInfo traceInfo;
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String baseUrl;
	
	@Autowired
	public EUWebDealerCalendarService(TraceInfo traceInfo, MutualAuthRestTemplate mutualAuthRestTemplate,
			@Qualifier("OSB_DATAPOWER") CredentialsSource euOsbCredentialsSource) {
		this.traceInfo = traceInfo;
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = euOsbCredentialsSource.getBaseUri();
	}

	@Override
	public DealerCalendarV2 getCalendar(String dealerCode, String marketCode, String locale, String modelName,
										List<String> additionalService, String motService) {
		
		DealerCalendarRequest request = createDealerCalendarRequest(dealerCode, marketCode, locale, modelName,
				additionalService, motService);
		EUWebDealerCalendarCommand command = getEUWebDealerCalendarCommand(request);
		EUOSBDealerCalendarResponse osbResponse = command.execute();
		if (osbResponse.getError() != null) {
			FordError error = new FordError(EU_OSB_STATUS_CONTEXT, Integer.parseInt(osbResponse.getError().getStatusCode()), osbResponse.getError().getCode());
			throw new BadRequestException(error);
		}
		return osbResponse.getData();
	}
	
	protected EUWebDealerCalendarCommand getEUWebDealerCalendarCommand(DealerCalendarRequest request) {
		
		return new EUWebDealerCalendarCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}

	protected DealerCalendarRequest createDealerCalendarRequest(String dealerCode, String marketCode, String locale, String modelName,
			List<String> additionalService, String motService) {

		return DealerCalendarRequest.builder()
				.dealerCode(dealerCode)
				.marketCode(marketCode)
				.locale(locale)
				.modelName(modelName)
				.additionalService(additionalService)
				.motService(motService)
				.build();
	}
}
