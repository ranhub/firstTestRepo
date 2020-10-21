package com.ford.turbo.servicebooking.models.osb.response.dealercalendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.OSBCCalendarLoadDealerDetailsData;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OSBDealerCalendarResponse extends OSBBaseResponse<OSBCCalendarLoadDealerDetailsData> {
}
