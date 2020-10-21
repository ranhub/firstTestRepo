package com.ford.turbo.servicebooking.service;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetails;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.models.osb.response.dealerdetails.DealerDetailsResponse;

public interface DealerDetailsService {
	default public DealerDetailsResponse getDealerDetails(List<String> dealerCode, String marketcode) {
		throw new NoBackendAvailableException();
	}
	
	public List<DealerDetails> getDealerDetails(DealersDetailsRequest request);
}
