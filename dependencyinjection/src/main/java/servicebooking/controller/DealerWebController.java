package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.annotation.Authorization;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetails;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetailsWrapper;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.models.msl.response.DealerDetailsWebResponse;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWebResponse;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.models.osb.DealerCalendarV2Response;
import com.ford.turbo.servicebooking.service.DealerCalendarV2Service;
import com.ford.turbo.servicebooking.service.DealerDetailsService;
import com.ford.turbo.servicebooking.service.DealerService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.EU;

@Authorization(authenticate = false, regions = EU)
@RestController
@RequestMapping(value = "/v1/public", produces = "application/json")
public class DealerWebController {
	
	private static final String VIN_17_CHARS_CAPS_ALPHANUMERIC = "[A-Z0-9]{17}";
	
	private DealerDetailsService dealerDetailsService; 
	private DealerService dealerService;
	private DealerCalendarV2Service dealerCalendarV2Service;
	
	@Autowired
	public DealerWebController(DealerDetailsService dealerDetailsService, DealerService dealerService, DealerCalendarV2Service dealerCalendarV2Service) {
		this.dealerDetailsService = dealerDetailsService;
		this.dealerService = dealerService;
		this.dealerCalendarV2Service = dealerCalendarV2Service;
	}

	@ApiOperation(value = "Gets a detailed list of dealer details and services offered", 
			notes = "[EU Web] Search for services offered by dealer using locale, marketCode, dealerCodes, "
						+ "vin, registrationNumber, modelName and buildYear",
			httpMethod = "POST")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Missing required parameters"),
			@ApiResponse(code = 403, message = "-Invalid Application-Id"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@PostMapping(value = "/dealers/details")
	public ResponseEntity<DealerDetailsWebResponse> getDealerDetails(
			@ApiIgnore HttpServletRequest request,
			@RequestBody @Valid DealersDetailsRequest dealersDetailsRequest) {
		validateDealerDetailsRequest(dealersDetailsRequest);
		
		DealerDetailsWebResponse dealerDetailsResponse = new DealerDetailsWebResponse();
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		dealerDetailsResponse.setLastRequested(lastRequested);
		dealerDetailsResponse.setRequestStatus(RequestStatus.CURRENT);
		
		DealerDetailsWrapper wrapper = DealerDetailsWrapper.builder().build();
		List<DealerDetails> dealerDetailsList = dealerDetailsService.getDealerDetails(dealersDetailsRequest);
		wrapper.setDealers(dealerDetailsList);
		dealerDetailsResponse.setValue(wrapper);
		return new ResponseEntity<DealerDetailsWebResponse>(dealerDetailsResponse, HttpStatus.OK);
	}

	@ApiOperation(value = "Returns all available time slots for a dealer", 
			notes = "[EU Web] Gets available time slots of a dealer using marketCode, locale, dealerCode, modelName, additionalServices and motService",
			httpMethod = "GET")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Missing required parameters"),
			@ApiResponse(code = 403, message = "-Invalid Application-Id"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@GetMapping(value = "/dealers/{dealerCode}/calendar")
	public ResponseEntity<DealerCalendarV2Response> getCalendarDetails(
			@PathVariable @NotEmpty String dealerCode,
			@RequestParam(required = true) String marketCode, 
			@RequestParam(required = true) String locale,
			@RequestParam(required = false) String modelName,
			@RequestParam(required = false) List<String> additionalService,
			@RequestParam(required = false) String motService) {
		
		validateDealerCalendarRequest(marketCode, locale);
		DealerCalendarV2Response calendarResponse = new DealerCalendarV2Response();
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		calendarResponse.setLastRequested(lastRequested);
		calendarResponse.setRequestStatus(RequestStatus.CURRENT);
		
		DealerCalendarV2 wrapper = dealerCalendarV2Service.getCalendar(dealerCode, marketCode, locale, modelName, additionalService, motService);
		calendarResponse.setValue(wrapper);

		return new ResponseEntity<DealerCalendarV2Response>(calendarResponse, HttpStatus.OK);
	}

	@ApiOperation(value = "Gets a detailed list of dealer services offered", 
			notes = "[EU Web] Get list of services offered by dealer using locale, marketCode, dealerCodes, voucherCode "
						+ "vin, registrationNumber, modelName and buildYear", 
			httpMethod = "GET")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "-Missing required parameters"),
			@ApiResponse(code = 403, message = "-Invalid Application-Id"),
			@ApiResponse(code = 500, message = "-Internal server error"),
			@ApiResponse(code = 502, message = "-Unexpected error from backend system"),
			@ApiResponse(code = 503, message = "-Backend service temporarily unavailable (circuit breaker open)"),
			@ApiResponse(code = 504, message = "-Backend system did not respond within time limit") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })
	@GetMapping(value = "/dealers/{dealerCode}/services")
	public ResponseEntity<DealerServicesWebResponse> getDealerServices(
			@ApiIgnore HttpServletRequest request,
			@PathVariable(required = true) String dealerCode,
			@RequestParam(required = true) String marketCode, 
			@RequestParam(required = true) String locale,
			@RequestParam(required = false) String modelName,
			@RequestParam(required = false) String buildYear, 
			@RequestParam(required = false) String vin, 
			@RequestParam(required = false) String registrationNumber, 
			@RequestParam(required = false) List<String> voucherCode) {
		
		
		DealerServicesRequest dealerServicesRequest = DealerServicesRequest.builder()
														.dealerCode(dealerCode)
														.marketCode(marketCode)
														.locale(locale)
														.buildYear(buildYear)
														.modelName(modelName)
														.vin(vin)
														.registrationNumber(registrationNumber)
														.voucherCode(voucherCode)
														.build();
		
		validateDealerServiceRequest(dealerServicesRequest);
		
		DealerServicesWebResponse dealerServicesResponse = new DealerServicesWebResponse();
		DealerServicesWebWrapper wrapper = dealerService.getDealerServices(dealerServicesRequest);
		dealerServicesResponse.setValue(wrapper);
		
		ZonedDateTime lastRequested = ZonedDateTime.now(ZoneOffset.UTC);
		dealerServicesResponse.setLastRequested(lastRequested);
		dealerServicesResponse.setRequestStatus(RequestStatus.CURRENT);

		return new ResponseEntity<DealerServicesWebResponse>(dealerServicesResponse, HttpStatus.OK);
	}
	
	protected void validateDealerServiceRequest(DealerServicesRequest request) {
		
		validateLocale(request.getLocale());
		validateDealerCode(request.getDealerCode());
		validateMarketCode(request.getMarketCode());
		validateVin(request.getVin());
		validateAtLeastVin_orRegNo_orModelNameAndBuildYear(request.getVin(), request.getRegistrationNumber(),
				request.getModelName(), request.getBuildYear());
	}

	protected void validateDealerDetailsRequest(DealersDetailsRequest dealersDetailsRequest) {
		validateLocale(dealersDetailsRequest.getLocale());
		validateMarketCode(dealersDetailsRequest.getMarketCode());
		validateDealerCodes(dealersDetailsRequest.getDealerCodes());
		validateVin(dealersDetailsRequest.getVin());
		validateAtLeastVin_orRegNo_orModelNameAndBuildYear(dealersDetailsRequest.getVin(), dealersDetailsRequest.getRegistrationNumber(),
				dealersDetailsRequest.getModelName(), dealersDetailsRequest.getBuildYear());
	}
	
	protected void validateDealerCalendarRequest(String marketCode, String locale) {
		
		validateMarketCode(marketCode);
		validateLocale(locale);
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
	
	protected void validateDealerCodes(List<String> dealerCodes) {
		if (dealerCodes == null || dealerCodes.isEmpty()) {
			throwBadRequestException("Required List parameter 'dealerCodes' is empty");
		}
	}
	
	protected void validateVin(String vin) {
		if (!StringUtils.isBlank(vin)) {
			if (!vin.matches(VIN_17_CHARS_CAPS_ALPHANUMERIC)) {
				InvalidVinException invalidVinException = new InvalidVinException(vin);
				throw invalidVinException;
			}
		}
	}
	
	protected void validateAtLeastVin_orRegNo_orModelNameAndBuildYear(String vin, String registrationNumber,
			String modelName, String buildYear) {
		if (StringUtils.isBlank(vin) && StringUtils.isBlank(registrationNumber) && (StringUtils.isBlank(modelName) || StringUtils.isBlank(buildYear))) {
			throwBadRequestException("Either [vin], or [registration number], or [model name and build year]  must be present");
		}
	}
	
	protected void validateDealerCode(String dealerCode) {
		if (StringUtils.isBlank(dealerCode)) {
			throwBadRequestException("Required String parameter 'dealerCode' is empty");
		}		
	}
	
	protected void throwBadRequestException(String message) {
		FordError fordError = new FordError(StatusContext.HTTP.getStatusContext(), HttpStatus.BAD_REQUEST.value(),message);
		throw new BadRequestException(fordError);
	}
}
