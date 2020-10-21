package servicebooking.aposb.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class APOSBCancelBookingResponse {
	private String status;
	private String version;
	private String statusDesc;

}
