package com.ford.turbo.servicebooking.models.msl.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetBookingsWebWrapper {
	private List<GetBookingsData> bookings;
}
