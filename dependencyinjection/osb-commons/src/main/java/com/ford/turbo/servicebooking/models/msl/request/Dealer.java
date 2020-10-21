package com.ford.turbo.servicebooking.models.msl.request;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("DealerRequest")
public class Dealer {
	private String name;
	private String phone;
	private String address;
}
