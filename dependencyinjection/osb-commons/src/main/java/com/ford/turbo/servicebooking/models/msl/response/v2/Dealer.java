package com.ford.turbo.servicebooking.models.msl.response.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ford.turbo.servicebooking.models.osb.DealerProfile;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ApiModel("_Dealer")
public class Dealer {

    private String name;
    private String id;
    private String street;
    private String town;
    private String postalCode;
    private String phone;
    private String address;


    public Dealer(DealerProfile dealerProfile) {
    	this.id = dealerProfile.getDealerCode();
        this.name = dealerProfile.getDealerName();
        this.street = dealerProfile.getStreet();
        this.town = dealerProfile.getTown();
        this.postalCode = dealerProfile.getPostalCode();
        this.phone = dealerProfile.getPhone();
    }
 
}
