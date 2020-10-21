package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EUOSBAccessCodesNotificationResponse extends OSBBaseResponse<EUOSBAccessCodesNotificationResponseData> {

	private EUOSBWebError error;
}
