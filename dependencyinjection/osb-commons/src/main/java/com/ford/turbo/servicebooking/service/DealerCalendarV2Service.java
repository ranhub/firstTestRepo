package com.ford.turbo.servicebooking.service;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;

public interface DealerCalendarV2Service {
	default public DealerCalendarV2 getCalendar(String dealerCode, String bookingDate, String applicationId) {
		throw new NoBackendAvailableException();
	}
	
	default public DealerCalendarV2 getCalendarWithSource(String dealerCode, String bookingDate, String source) {
		throw new NoBackendAvailableException();
	}
	
	default public DealerCalendarV2 getCalendar(String dealerCode, String marketCode, String locale, String modelName, List<String> additionalService, String motService) {
		throw new NoBackendAvailableException();
	}
}
