package com.ford.turbo.servicebooking.models.msl.response;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetailsWrapper;

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
public class DealerDetailsWebResponse extends BaseResponse {
	private DealerDetailsWrapper value;
}
