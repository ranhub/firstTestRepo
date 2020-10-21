package com.ford.turbo.servicebooking.models.msl.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequest extends CreateBookingRequest {
	private String bookingId;
	private String appId;
}