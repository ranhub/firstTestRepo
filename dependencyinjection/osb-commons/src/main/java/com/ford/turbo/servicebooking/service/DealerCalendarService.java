package com.ford.turbo.servicebooking.service;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarResponse;

public interface DealerCalendarService {

	default public DealerCalendarResponse getCalendar(String dealerCode, String marketCode, List<String> additionalServiceIds) {
		throw new NoBackendAvailableException();
	}
}
