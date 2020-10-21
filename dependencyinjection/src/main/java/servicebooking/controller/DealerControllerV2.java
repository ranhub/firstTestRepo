package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.msl.response.ServiceAdvisorDetails;
import com.ford.turbo.servicebooking.models.msl.response.ServiceAdvisorResponse;
import com.ford.turbo.servicebooking.service.DealerService;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.AP;

@RestController
@RequestMapping(value = "/v2", produces = "application/json")
public class DealerControllerV2 {
	private final AuthTokenValidator authTokenValidator;
	private final DealerService dealerService;
	private ServiceBookingUtils serviceBookingUtils;

	@Autowired
	public DealerControllerV2(AuthTokenValidator authTokenValidator, DealerService dealerService, ServiceBookingUtils serviceBookingUtils) {
		this.authTokenValidator = authTokenValidator;
		this.dealerService = dealerService;
		this.serviceBookingUtils = serviceBookingUtils;
	}

	@ApiOperation(value = "Gets a list of Service Advisors at a dealership", 
				  httpMethod = "GET",
				  notes = "[AP] Returns a list of Service Advisors for a dealership based on the provided dealer id.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
	@GetMapping(value = "dealers/{dealerId}/serviceadvisors")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	public ResponseEntity<ServiceAdvisorResponse> getServiceAdvisors(HttpServletRequest request,
			@PathVariable String dealerId) {

		ServiceAdvisorResponse serviceAdvisorResponse = new ServiceAdvisorResponse();
		
		if(StringUtils.isBlank(dealerId)){
			serviceAdvisorResponse.setError(new FordError("Marketing Services Layer", HttpStatus.BAD_REQUEST.value(), "Missing Dealer Id"));
			serviceAdvisorResponse.setRequestStatus(RequestStatus.UNAVAILABLE);
			return new ResponseEntity<>(serviceAdvisorResponse, HttpStatus.BAD_REQUEST);
		}
		
		ZonedDateTime lastRequested = ZonedDateTime.now();

		String authToken = request.getHeader("auth-token");
		String appId = request.getHeader("Application-id");

		authTokenValidator.checkValid(authToken, appId);
		serviceBookingUtils.validateApplicationId(appId, Arrays.asList(AP));
		
		List<ServiceAdvisorDetails> serviceAdvisorsList = dealerService.getDealerServiceAdvisors(dealerId);
		serviceAdvisorResponse = ServiceAdvisorResponse.builder()
															.value(serviceAdvisorsList)
														.build();
		
		serviceAdvisorResponse.setLastRequested(lastRequested);
		serviceAdvisorResponse.setRequestStatus(RequestStatus.CURRENT);

		return new ResponseEntity<>(serviceAdvisorResponse, HttpStatus.OK);
	}
}
