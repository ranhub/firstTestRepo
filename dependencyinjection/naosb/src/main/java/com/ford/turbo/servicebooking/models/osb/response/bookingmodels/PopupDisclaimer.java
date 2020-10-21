package com.ford.turbo.servicebooking.models.osb.response.bookingmodels;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupDisclaimer {
    @JsonProperty("Abstract")
    private String abstractField;
    @JsonProperty("Title")
    private String title;
}
