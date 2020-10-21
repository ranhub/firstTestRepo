package com.ford.turbo.servicebooking.utils;

import java.util.HashMap;
import java.util.Map;

import com.ford.turbo.aposb.common.basemodels.input.CountryCode;

public class CountryCodeToEcatMapper {
    private static Map<CountryCode, String> countryCodeToEcatMap ;

    static {
        countryCodeToEcatMap = new HashMap<>();
        countryCodeToEcatMap.put(CountryCode.GBR,"GB");
        countryCodeToEcatMap.put(CountryCode.DEU,"DE");
        countryCodeToEcatMap.put(CountryCode.FRA,"FR");
        countryCodeToEcatMap.put(CountryCode.ITA, "IT");
        
    }

    public static String getEcatCode(CountryCode countryCode) {
        return countryCodeToEcatMap.get(countryCode);
    }

}
