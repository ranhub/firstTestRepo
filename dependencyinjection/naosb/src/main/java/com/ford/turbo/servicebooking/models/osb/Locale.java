package com.ford.turbo.servicebooking.models.osb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Locale {
    private String country;
    private String displayCountry;
    private String displayLanguage;
    private String displayName;
    private String displayVariant;
    private String iSO3Country;
    private String iSO3Language;
    private String language;
    private String variant;
}
