package servicebooking.aposb.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APOSBCreateBookingResponse {
	private String status;
	private String version;
	private String statusDesc;
	private String orderId;
}
