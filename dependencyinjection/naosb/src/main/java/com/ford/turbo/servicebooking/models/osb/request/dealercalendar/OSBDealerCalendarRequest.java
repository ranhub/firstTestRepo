package com.ford.turbo.servicebooking.models.osb.request.dealercalendar;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OSBDealerCalendarRequest {
    private String dealerCode;
    private String marketCode;
    private List<OSBAdditionalService> selectedAdditionalServices;
}
