package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.msl.response.BookingMSLResponse;
import com.ford.turbo.servicebooking.models.msl.response.BookingsResponse;
import com.ford.turbo.servicebooking.models.msl.response.ServicesListResponse;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.CancelBookedServiceResponse;
import com.ford.turbo.servicebooking.service.BookingService;
import com.ford.turbo.servicebooking.service.ListDetailInformationService;
import com.ford.turbo.servicebooking.service.UserProfileService;
import com.ford.turbo.servicebooking.utils.DealerCodeFormatter;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1", produces = "application/json")
public class ServiceBookingController {
    private static final String VIN_17_CHARS_CAPS_ALPHANUMERIC = "[A-Z0-9]{17}";

    private final AuthTokenValidator authTokenValidator;
    private final ListDetailInformationService listDetailInformationService;
    private final UserProfileService userProfileService;
    private final ServiceBookingUtils serviceBookingUtils;
    private final BookingService bookingService;
 
    @Autowired
    public ServiceBookingController(AuthTokenValidator authTokenValidator, 
                                    ListDetailInformationService listDetailInformationService, 
                                    UserProfileService userProfileService,
                                    ServiceBookingUtils serviceBookingUtils,
                                    BookingService bookingService) {
        this.authTokenValidator = authTokenValidator;
        this.listDetailInformationService = listDetailInformationService;
        this.userProfileService = userProfileService;
        this.serviceBookingUtils= serviceBookingUtils;
        this.bookingService = bookingService;
    }

    @ApiOperation(
            value = "Returns booking details for the user.",
            httpMethod = "GET",
            notes = "[EU]  1. If VIN is passed,  returns booking details for the VIN, if VIN is found in the booking list for the user."
            		+ "    2. If VIN is not passed, returns booking details for all the vins for the given user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @GetMapping(value = "/bookings")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")
    })
    public ResponseEntity<BookingsResponse> getBookings(
            HttpServletRequest request,
            @ApiParam(value = "2-3 character market code", required = true) @RequestParam String marketCode,
            @ApiParam(value = "17 character vin", required = false) @RequestParam(value = "vin", required = false)
            Optional<String> vin) {
        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        if (vin.isPresent()) {
            validateVin(vin.get());
        }
        
    	serviceBookingUtils.validateApplicationId(appId);

        UserProfile userProfile = userProfileService.getUserProfile(authToken, appId);
        BookedServiceResponse response = bookingService.listServiceBookings(userProfile.getUserId(), marketCode, vin);

        BookingsResponse success = BookingsResponse.success(response, lastRequested, false);

        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Creates a Booking for a user.",
            httpMethod = "POST",
            notes = "[EU, AP] Creates booking for a user using given information."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @PostMapping(value = "/bookings")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")})
    public ResponseEntity<BookingMSLResponse> createServiceBooking(
            HttpServletRequest request, @RequestBody CreateBookingRequest requestBody) throws Exception {

    	
        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

		authTokenValidator.checkValid(authToken, appId);
        serviceBookingUtils.validateApplicationId(appId);
        
        BookingMSLResponse response = new BookingMSLResponse();
        
        String referenceNumber = bookingService.createBooking(requestBody, appId, authToken);
		
        response.setBookingCustomerRefNum(referenceNumber);
        
	    response.setLastRequested(lastRequested);
        response.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        
			
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Cancel a previously Booked Service using reference number.",
            httpMethod = "DELETE",
            notes = "[EU, AP] Cancels a booked service mapped to given reference number."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/bookings", method = RequestMethod.DELETE)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")})
    public ResponseEntity<CancelBookedServiceResponse> cancelBookedService(
            HttpServletRequest request,
            @ApiParam(value = "2-3 character market code") @RequestParam(required = false) String marketCode,
            @ApiParam(value = "Booked Service Reference Number", required = true) @RequestParam String referenceNumber) throws Exception {

        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");
        
        authTokenValidator.checkValid(authToken, appId);
        
        serviceBookingUtils.validateApplicationId(appId);

        CancelBookedServiceResponse response = bookingService.cancelBooking(referenceNumber, marketCode, appId, authToken);
        
        response.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        response.setLastRequested(lastRequested);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(
            value = "List Available Services provided by dealer for given VIN.",
            httpMethod = "GET",
            notes = "[EU] Gets all available services, for user provided information."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @GetMapping(value = "/services")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")})
    public ResponseEntity<ServicesListResponse> getServices(
            HttpServletRequest request,
            @ApiParam(value = "Language to get the descriptions in", required = true) @RequestParam LanguageCode language,
            @ApiParam(value = "2 letter language region code", required = true) @RequestParam RegionCode region,
            @ApiParam(value = "3 letter country code", required = true) @RequestParam CountryCode country,
            @ApiParam(value = "Mileage", required = true) @RequestParam String mileage,
            @ApiParam(value = "17 character vin", required = true) @RequestParam String vin,
            @ApiParam(value = "Dealer code", required = true) @RequestParam String dealerCode,
            @ApiParam(value = "Voucher code", required = false) @RequestParam(required=false) List<String> voucherCode) {

        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        validateVin(vin);
        
        serviceBookingUtils.validateApplicationId(appId);

        ServicesListResponse response = listDetailInformationService.listServices(language, region, country, mileage, vin, DealerCodeFormatter.formatDealerCode(dealerCode), voucherCode);
        response.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        response.setLastRequested(lastRequested);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void validateVin(String vin) throws InvalidVinException {
        if (!vin.matches(VIN_17_CHARS_CAPS_ALPHANUMERIC)) {
            throw new InvalidVinException(vin);
        }
    }
}
