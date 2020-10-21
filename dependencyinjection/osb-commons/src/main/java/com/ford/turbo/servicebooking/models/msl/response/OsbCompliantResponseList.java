package com.ford.turbo.servicebooking.models.msl.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OsbCompliantResponseList extends BaseResponse {

    private List<OsbCompliantDealerResponse > values;

	public List<OsbCompliantDealerResponse> getValues() {
		return values;
	}

	public void setValues(List<OsbCompliantDealerResponse> values) {
		this.values = values;
	}
    
}
