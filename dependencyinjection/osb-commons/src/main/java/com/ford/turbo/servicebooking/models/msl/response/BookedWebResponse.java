package com.ford.turbo.servicebooking.models.msl.response;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedWebResponse extends BaseResponse{
	private GetBookingsWebWrapper value;
}
