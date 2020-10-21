package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUOSBCustomerResponse {

	private String lastName;
	private String title;
	private String contactNumber;
	private String firstName;
	private String email;
	private String phone;
}
