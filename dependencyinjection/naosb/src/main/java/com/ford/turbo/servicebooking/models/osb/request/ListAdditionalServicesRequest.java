package com.ford.turbo.servicebooking.models.osb.request;

import java.util.List;

import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.APSelectedEcatService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAdditionalServicesRequest {
    private String marketCode;
    private String locale;
    private String selectedVehicleLineCode;
    private List<APDealer> dealers;
    private APSelectedEcatService selectedEcatService;
}
