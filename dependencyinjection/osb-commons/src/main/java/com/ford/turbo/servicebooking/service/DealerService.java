package com.ford.turbo.servicebooking.service;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.eu.web.Dealer;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.ServiceAdvisorDetails;

public interface DealerService {
	default public List<ServiceAdvisorDetails> getDealerServiceAdvisors(String dealerId) {
		throw new NoBackendAvailableException();
	}
	
	public List<Dealer> getDealersbyMarketCode(String marketCode);
	
	public DealerServicesWebWrapper getDealerServices(DealerServicesRequest request); 
}
