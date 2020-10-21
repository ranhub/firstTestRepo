package com.ford.turbo.servicebooking.models.msl.response.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedServiceResponseValue {

	private String bookingId;
	private BookingAppointment appointment;
	private String vin;
	private String vehicleName;
	private String serviceType;
	private String osbStatus;
	
}
