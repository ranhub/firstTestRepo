package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.OSBBaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EUOSBCreateBookingResponse extends OSBBaseResponse<CreateBookingWebWrapper> {

	private EUOSBWebError error;
}
