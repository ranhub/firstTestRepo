package com.ford.turbo.servicebooking.service.eu.web;

import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetails;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.models.osb.response.dealerdetails.DealerDetailsResponse;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.DealerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.LoadOsbSpecificDealerDetailsCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUDealerDetailsCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerDetailsResponse;

@Service
public class EUDealerDetailsService implements DealerDetailsService {
	
	private final String EU_OSB_STATUS_CONTEXT = "EU OSB";

    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private TraceInfo traceInfo;
    private CredentialsSource credentialsSource;
    private String baseUrl;

    @Autowired
    public EUDealerDetailsService(MutualAuthRestTemplate mutualAuthRestTemplate, TraceInfo traceInfo, @Qualifier("OSB_DATAPOWER") CredentialsSource credentialsSource) {
        this.mutualAuthRestTemplate = mutualAuthRestTemplate;
        this.traceInfo = traceInfo;
        this.credentialsSource = credentialsSource;
        this.baseUrl = credentialsSource.getBaseUri();
    }

    @Override
    public DealerDetailsResponse getDealerDetails(List<String> dealerCode, String marketcode) {

        LoadOsbSpecificDealerDetailsCommand loadOsbSpecificDealerDetailsCommand = new LoadOsbSpecificDealerDetailsCommand(traceInfo, mutualAuthRestTemplate, credentialsSource.getBaseUri(), dealerCode, marketcode);

        DealerDetailsResponse dealerDetailsResponse = loadOsbSpecificDealerDetailsCommand.execute();

        return dealerDetailsResponse;
    }
    
	@Override
	public List<DealerDetails> getDealerDetails(DealersDetailsRequest request) {
		
		EUDealerDetailsCommand command = getEUDealerDetailsCommand(request);
		EUOSBDealerDetailsResponse commandResponse = command.execute();
		if (commandResponse.getError() != null) {
			FordError error = new FordError(EU_OSB_STATUS_CONTEXT, Integer.parseInt(commandResponse.getError().getStatusCode()), commandResponse.getError().getCode());
			throw new BadRequestException(error);
		}
		return commandResponse.getData();
	}
	
	public EUDealerDetailsCommand getEUDealerDetailsCommand(DealersDetailsRequest request) {
		return new EUDealerDetailsCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}

}
