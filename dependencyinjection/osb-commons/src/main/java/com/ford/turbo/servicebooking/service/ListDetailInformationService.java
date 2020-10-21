package com.ford.turbo.servicebooking.service;

import java.util.List;

import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.servicebooking.models.msl.response.ServicesListResponse;

public interface ListDetailInformationService {
    default public ServicesListResponse listServices(LanguageCode language, RegionCode region, CountryCode country,
            String mileage, String vin, String dealerCode, List<String> voucherCodes) {
		throw new NoBackendAvailableException(); 
    }
}
