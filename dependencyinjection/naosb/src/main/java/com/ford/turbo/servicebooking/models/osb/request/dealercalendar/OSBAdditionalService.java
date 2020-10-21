package com.ford.turbo.servicebooking.models.osb.request.dealercalendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OSBAdditionalService {
    private String additionalServiceId;
    private String additionalServiceName;
}
