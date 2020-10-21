package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.annotation.Authorization;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.request.EUWebCustomer;
import com.ford.turbo.servicebooking.models.msl.response.*;
import com.ford.turbo.servicebooking.service.WebBookingService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.EU;

@Authorization(authenticate = false, regions = { EU })
@RestController
@RequestMapping(value = "/v1/public", produces = "application/json")
public class BookingWebController extends BookingWebExceptionHandler {
	
	private static final String VIN_17_CHARS_CAPS_ALPHANUMERIC = "[A-Z0-9]{17}";

	private WebBookingService service;

	@Autowired
	public BookingWebController(WebBookingService service) {
		this.service = service;
	}

	@ApiOperation(value = "Cancels booking for BookingReferenceNumber using AccessCode", 
			httpMethod = "DELETE", 
			notes = "[EU Web] This endpoint cancels booking with BookingReferenceNumber only when the provided access code matches the booking access code in the backend.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Bad request"),
			@ApiResponse(code = 403, message = "-Application ID invalid"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@DeleteMapping(value = "/bookings/{bookingReferenceNumber}")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	public ResponseEntity<CancelBookingWebResponse> cancelBooking(HttpServletRequest request,
			@PathVariable String bookingReferenceNumber,
			@RequestParam(required = true) String accessCode, @RequestParam(required = true) boolean osbSiteTermsRequired) throws Exception {
		
		validateAccessCode(accessCode);
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		CancelBookingWebResponse response = CancelBookingWebResponse.builder().build();
		response.setLastRequested(lastRequested);
		response.setRequestStatus(RequestStatus.CURRENT);
		CancelBookingWebWrapper wrapper = service.cancelBooking(bookingReferenceNumber, accessCode, osbSiteTermsRequired);
		response.setValue(wrapper);
		return ResponseEntity.ok(response);
	}
	
	@ApiOperation(value = "Request Access Code to be sent for the given email and market code combination.", 
			httpMethod = "POST", 
			notes = "[EU Web] This endpoint will trigger email with the access code for booking made using the email address, so that customer can use it in future.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Bad request"),
			@ApiResponse(code = 403, message = "-Application ID invalid"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@PostMapping(value = "/bookings/sendAccessCodes")
	public ResponseEntity<AccessCodesNotificationWebResponse> accessCodes(@RequestBody @Valid AccessCodesNotificationRequest request) {
		
		validateAccessCodeNotificationRequest(request);
		AccessCodesNotificationWebWrapper wrapper = service.sendAccessCodesNotification(request);
		AccessCodesNotificationWebResponse response = new AccessCodesNotificationWebResponse();
		response.setValue(wrapper);
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		response.setLastRequested(lastRequested);
		response.setRequestStatus(RequestStatus.CURRENT);
		return ResponseEntity.ok(response);
	}
	
	@ApiOperation(value = "Retrieve bookings for a user, based on the provided email address and access code.", 
			httpMethod = "GET", 
			notes = "[EU Web] This endpoint will give details of booked services which customer can verify or request for changes using other endpoints")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Bad request"),
			@ApiResponse(code = 403, message = "-Application ID invalid"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@GetMapping(value = "/bookings")
	public ResponseEntity<BookedWebResponse> getBookings(
            	@ApiParam(value = "Access Code", required = true) @RequestParam(required = true) String accessCode,
            	@ApiParam(value = "Email Address", required = true) @RequestParam(required = true) String email) {
		
        validateAccessCode(accessCode);
        validateEmail(email);
        
		BookedWebResponse response = new BookedWebResponse();
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		
		GetBookingsWebWrapper wrapper = service.getBookings(accessCode, email);
		
		response.setValue(wrapper);
		response.setLastRequested(lastRequested);
		response.setRequestStatus(RequestStatus.CURRENT);
		
		return new ResponseEntity<BookedWebResponse>(response, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Creates an appointment for Service Booking using given information.", 
			httpMethod = "POST", 
			notes = "[EU Web] Creates service booking with given VIN/Registration Number/Model, build year combinations in the request.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Bad request"),
			@ApiResponse(code = 403, message = "-Application ID invalid"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@PostMapping(value = "/bookings")
	public ResponseEntity<CreateBookingWebResponse> createBooking(@Valid @NotNull @RequestBody CreateBookingWebRequest request) {
		
		validateCreateBookingRequest(request);
		CreateBookingWebResponse response = new CreateBookingWebResponse();
		CreateBookingWebWrapper wrapper = service.createBooking(request);
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		response.setLastRequested(lastRequested);
		response.setRequestStatus(RequestStatus.CURRENT);
		response.setValue(wrapper);
		
		return ResponseEntity.ok(response);
	}
	
	private void validateAccessCodeNotificationRequest(AccessCodesNotificationRequest request) {
		
		validateMarketCode(request.getMarketCode());
		validateEmail(request.getEmail());
		validateOsbSiteTermsRequired(request.getOsbSiteTermsRequired());
	}
	
	private void validateCreateBookingRequest(CreateBookingWebRequest request) {
		
		validateLocale(request.getLocale());
		validateMarketCode(request.getMarketCode());
		validateDealerCode(request.getDealerCode());
		validateOsbSiteTermsRequired(request.getOsbSiteTermsRequired());
		validateAppointmentTime(request.getAppointmentTime());
		validateServiceType(request.getServiceType());
		validateCustomer(request.getCustomer());
		validateVin(request.getVin());
		validateAtLeastVin_orRegNo_orModelNameAndBuildYear(request.getVin(), request.getRegistrationNumber(),
				request.getModelName(), request.getBuildYear());
	}
	
	private void validateLocale(String locale) {
		
		if (StringUtils.isBlank(locale)) {
			throwBadRequestException("Request parameter 'locale' must not be blank");
		}
	}
	
	private void validateMarketCode(String marketCode) {
		
		if (StringUtils.isBlank(marketCode)) {
			throwBadRequestException("Request parameter 'marketCode' must not be blank");
		}
	}

	private void validateDealerCode(String dealerCode) {
		
		if (StringUtils.isBlank(dealerCode)) {
			throwBadRequestException("Request parameter 'dealerCode' must not be blank");
		}
	}
	
	private void validateEmail(String email) {
		
		if (StringUtils.isBlank(email)) {
			throwBadRequestException("Request parameter 'email' must not be blank");
		}

	}
	
	private void validateAccessCode(String accessCode) {

		if (StringUtils.isBlank(accessCode)) {
			throwBadRequestException("Request parameter 'accessCode' must not be blank");
		}
	}
	
	private void validateOsbSiteTermsRequired(Boolean osbSiteTermsRequired) {
		
		if (osbSiteTermsRequired == null) {
			throwBadRequestException("Request parameter 'osbSiteTermsRequired' must not be null");
		}
	}
	
	private void validateAppointmentTime(String appointmentTime) {
		
		if (StringUtils.isBlank(appointmentTime)) {
			throwBadRequestException("Request parameter 'appointmentTime' must not be blank");
		}
	}

	private void validateServiceType(String serviceType) {
		
		if (StringUtils.isBlank(serviceType)) {
			throwBadRequestException("Request parameter 'serviceType' must not be blank");
		}
	}
	
	private void validateCustomer(EUWebCustomer customer) {
		
		if (customer == null) {
			throwBadRequestException("Request paramter 'customer' must not be null");
		}
	}
	
	private void validateVin(String vin) {
		if (!StringUtils.isBlank(vin)) {
			if (!vin.matches(VIN_17_CHARS_CAPS_ALPHANUMERIC)) {
				InvalidVinException invalidVinException = new InvalidVinException(vin);
				throw invalidVinException;
			}
		}
	}
	
	private void validateAtLeastVin_orRegNo_orModelNameAndBuildYear(String vin, String registrationNumber,
			String modelName, String buildYear) {
		if (StringUtils.isBlank(vin) && StringUtils.isBlank(registrationNumber) && (StringUtils.isBlank(modelName) || StringUtils.isBlank(buildYear))) {
			throwBadRequestException("Either [vin], or [registration number], or [model name and build year] must be present");
		}
	}
	
	protected void throwBadRequestException(String message) {
		
		FordError fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(),message);
		throw new BadRequestException(fordError);
	}

}
