package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.annotation.Authorization;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.eu.web.Dealer;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleFeaturesData;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWrapper;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;
import com.ford.turbo.servicebooking.models.msl.response.Dealers;
import com.ford.turbo.servicebooking.models.msl.response.DealersResponse;
import com.ford.turbo.servicebooking.models.msl.response.EUWebVehicleFeaturesResponse;
import com.ford.turbo.servicebooking.models.msl.response.EUWebVehicleLookupResponse;
import com.ford.turbo.servicebooking.service.DealerService;
import com.ford.turbo.servicebooking.service.WebServiceBookingService;
import com.ford.turbo.servicebooking.service.WebServiceVehicleFeatureService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;

import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.EU;

@Authorization(authenticate = false, regions = EU)
@RestController
@RequestMapping(value = "/v1/public", produces = "application/json")
public class ServiceBookingWebController {

	private static final String VIN_17_CHARS_CAPS_ALPHANUMERIC = "[A-Z0-9]{17}";

	private WebServiceBookingService euBookingService;
	private DealerService dealerService;
	private WebServiceVehicleFeatureService vehicleFeatureService;

	@Autowired
	public ServiceBookingWebController(
			WebServiceBookingService euBookingService, 
			WebServiceVehicleFeatureService vehicleFeatureService, 
			DealerService dealerService) {
		this.euBookingService = euBookingService;
		this.vehicleFeatureService = vehicleFeatureService;
		this.dealerService = dealerService;
	}

	@ApiOperation(value = "Vehicle Lookup by VIN and Registration Number", 
			httpMethod = "GET", 
			notes = "[EU Web] Return Vehicle information by VIN and Registration Number, return error when  VIN or Registration Number invalid")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Bad request"),
			@ApiResponse(code = 403, message = "-Application ID invalid"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@GetMapping(value = "/vehicleLookup")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	public ResponseEntity<EUWebVehicleLookupResponse> vehicleLookup(HttpServletRequest request,
			@RequestParam(required = false) String vin, @RequestParam(required = false) String registrationNumber,
			@RequestParam(required = true) String locale, @RequestParam(required = true) String marketCode,
			@RequestParam(required = true) long mileage, @RequestParam(required = true) String ecatMarketCode,
			@RequestParam(required = true) boolean osbSiteTermsRequired) throws Exception {

		validateVinAndRegistrationNumber(vin, registrationNumber);

		VehicleDetailsWrapper vehicleDetails = euBookingService.getVehicleLookup(vin, registrationNumber, locale,
				marketCode, mileage, ecatMarketCode, osbSiteTermsRequired);
		
		EUWebVehicleLookupResponse response = EUWebVehicleLookupResponse.builder().build();
		ZonedDateTime lastRequested = ZonedDateTime.now();
		response.setLastRequested(lastRequested);
		response.setRequestStatus(RequestStatus.CURRENT);
		response.setValue(vehicleDetails);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Returns Vehicle Features by locale and market code", 
			notes = "[EU Web] Gets all the vehicle features for provided locale and market code", 
			httpMethod = "GET")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@GetMapping(value = "/vehicleFeatures")

	public ResponseEntity<EUWebVehicleFeaturesResponse> getVehicleFeatures(HttpServletRequest request,
			@ApiParam(value = "locale is required", required = true) @RequestParam(value = "locale", required = true) String locale,
			@ApiParam(value = "market code is required", required = true) @RequestParam(value = "marketCode", required = true) String marketCode)
			throws Exception {

		validateVehicleFeaturesRequest(locale, marketCode);

		EUWebVehicleFeaturesRequest euWebVehicleFeaturesRequest = EUWebVehicleFeaturesRequest.builder().locale(locale)
				.marketCode(marketCode).build();

		EUWebVehicleFeaturesData euVehicleFeaturesData = vehicleFeatureService
				.getVehicleFeatures(euWebVehicleFeaturesRequest);

		EUWebVehicleFeaturesResponse euWebVehicleFeaturesResponse = EUWebVehicleFeaturesResponse.builder()
				.value(euVehicleFeaturesData).build();

		ZonedDateTime lastRequested = ZonedDateTime.now();
		euWebVehicleFeaturesResponse.setLastRequested(lastRequested);
		euWebVehicleFeaturesResponse.setRequestStatus(RequestStatus.CURRENT);

		return new ResponseEntity<>(euWebVehicleFeaturesResponse, HttpStatus.OK);
	}

	@ApiOperation(value = "Returns Dealer Data for market code", 
			notes = "[EU Web] Gets all the dealer names and dealer codes for provided market code", 
			httpMethod = "GET")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@GetMapping(value = "/dealers")
	public ResponseEntity<DealersResponse> getDealerData(HttpServletRequest request, @RequestParam(value = "marketCode", required = true) String marketCode) {
		
		validateMarketCode(marketCode);
		
		List<Dealer> dealerList = dealerService.getDealersbyMarketCode(marketCode);
		Dealers dealers = Dealers.builder().dealers(dealerList).build();
		
		DealersResponse dealersResponse = new DealersResponse(dealers);
		ZonedDateTime lastRequested = ZonedDateTime.now();
		dealersResponse.setLastRequested(lastRequested);
		dealersResponse.setRequestStatus(RequestStatus.CURRENT);

		return new ResponseEntity<DealersResponse>(dealersResponse, HttpStatus.OK);
	}
	
	private void validateVinAndRegistrationNumber(String vin, String registrationNumber)
			throws InvalidVinException, BadRequestException {

		if (!StringUtils.isBlank(vin)) {
			if (!vin.matches(VIN_17_CHARS_CAPS_ALPHANUMERIC)) {
				InvalidVinException invalidVinException = new InvalidVinException(vin);
				throw invalidVinException;
			}
		}
		
		if (StringUtils.isBlank(vin) && StringUtils.isBlank(registrationNumber)) {
			throwBadRequestException("Both VIN and Registration number are missing, at least one must be present");
		}
	}

	protected void validateVehicleFeaturesRequest(String locale, String marketCode) {
		validateLocale(locale);
		validateMarketCode(marketCode);
	}
	
	protected void validateLocale(String locale) {
		if (StringUtils.isBlank(locale)) {
			throwBadRequestException("Required String parameter 'locale' is empty");
		}
	}
	
	protected void validateMarketCode(String marketCode) {
		if (StringUtils.isBlank(marketCode)) {
			throwBadRequestException("Required String parameter 'marketCode' is empty");
		}
	}
	
	protected void throwBadRequestException(String message) {
		FordError fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(),message);
		throw new BadRequestException(fordError);
	}
}
