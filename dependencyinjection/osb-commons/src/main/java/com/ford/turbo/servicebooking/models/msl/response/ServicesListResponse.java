package com.ford.turbo.servicebooking.models.msl.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.VehicleDetails;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServicesListResponse extends BaseResponse {

    private List<MainService> main = new ArrayList<>();
    private List<AdditionalService> additional = new ArrayList<>();
    private List<OSBOVService> oldServices = new ArrayList<>();
    
    @JsonIgnore
    private VehicleDetails vehicleDetails;
    
	
}
