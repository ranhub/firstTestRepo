package com.ford.turbo.servicebooking.models.msl.response.v2;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

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
public class BookedServiceV2Response extends BaseResponse {
	private String status;
	private String version;
	private String statusDesc;
	
	private List<BookedServiceResponseValue> value;
}
