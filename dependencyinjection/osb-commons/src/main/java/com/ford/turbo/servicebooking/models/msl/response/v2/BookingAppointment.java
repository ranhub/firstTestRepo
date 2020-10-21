package com.ford.turbo.servicebooking.models.msl.response.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAppointment {
	private String date;
	private String timeSlot;
}
