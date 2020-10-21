package com.ford.turbo.servicebooking.models.msl.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EUWebCustomer {
	private String title;
	private String firstName;
	private String lastName;
	private String contactNumber;
	private String email;
}
