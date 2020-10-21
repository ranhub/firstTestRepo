package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.ServiceFluid;
import com.ford.turbo.servicebooking.models.osb.ServiceLabour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainService {
    private String serviceIntervalInKM;
    private PopupDisclaimer popupDisclaimer;
    private String serviceDescription;
    private String serviceFamilyCSP;
    private ServicePart servicePart;
    private BigDecimal price;
    private String mainServiceCode;
    private String serviceType;
    private ServiceLabour serviceLabour;
    private String serviceFamilySARA;
    private String serviceIntervalInYears;
    private Boolean selected;
    private String applicationInformation;
    private List<ServiceFluid> serviceFluids;
    private List<FixedPrice> fixedPrices;
    private String serviceIntervalInMiles;
}
