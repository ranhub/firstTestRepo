package com.ford.turbo.servicebooking.models.msl.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OsbCompliantDealerResponse{
    private boolean isCompliant;
    private String dealerCode;
}
