package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import java.util.List;

import com.ford.turbo.servicebooking.models.eu.web.Dealer;
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
public class EUOSBDealersResponse extends OSBBaseResponse<List<Dealer>> {
	private EUOSBWebError error;

}
