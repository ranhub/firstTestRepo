package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;
import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.servicebooking.models.msl.request.UpdateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.response.BookingMSLResponse;
import com.ford.turbo.servicebooking.models.msl.response.v2.BookedServiceV2Response;
import com.ford.turbo.servicebooking.models.msl.response.v2.BookingDetailsServiceResponse;
import com.ford.turbo.servicebooking.service.BookingService;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/v2", produces = "application/json")
public class ServiceBookingControllerV2 {

    private final AuthTokenValidator authTokenValidator;
    private final ServiceBookingUtils serviceBookingUtils;
    private final BookingService bookingService;
    
    @Autowired
    public ServiceBookingControllerV2(AuthTokenValidator authTokenValidator, ServiceBookingUtils serviceBookingUtils, BookingService bookingService) {
        this.authTokenValidator = authTokenValidator;
        this.serviceBookingUtils= serviceBookingUtils;
        this.bookingService = bookingService;
    }

    @ApiOperation(
            value = "Returns Booking Information for user",
            httpMethod = "GET",
            notes = "[AP] Gets service booked information for user."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @GetMapping(value = "bookings")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")
    })
    public ResponseEntity<BookedServiceV2Response> getBookings(HttpServletRequest request) throws Exception {

        ZonedDateTime lastRequested = ZonedDateTime.now();
        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        UserIdentity userIdentity = authTokenValidator.checkValid(authToken, appId);
        serviceBookingUtils.validateApplicationId(appId, Arrays.asList(ContinentCode.AP));
        
        BookedServiceV2Response response = bookingService.getUserBookings(appId, authToken, userIdentity); 
        
        response.setLastRequested(lastRequested);
        response.setRequestStatus(RequestStatus.CURRENT);   

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Returns Booking Details Information for booking id",
            httpMethod = "GET",
            notes = "[AP] Gets booking details for provided booking id"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @GetMapping(value = "bookings/{bookingId}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")
    })
    public ResponseEntity<BookingDetailsServiceResponse> getBookingDetails(
            HttpServletRequest request,
            @PathVariable String bookingId
            ) {

        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
                
    	serviceBookingUtils.validateApplicationId(appId, Arrays.asList(ContinentCode.AP));
        
    	BookingDetailsServiceResponse response = BookingDetailsServiceResponse.builder()
    			.value(bookingService.getBookingDetails(bookingId,appId))
    											.build();
        response.setLastRequested(lastRequested);
        response.setRequestStatus(RequestStatus.CURRENT);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
          
    @ApiOperation(
            value = "Updates Booked Service for booking id",
            httpMethod = "PUT",
            notes = "[AP] Updates a previously booked service for provided booking id"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @PutMapping(value = "bookings/{bookingId}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")})
    public ResponseEntity<BookingMSLResponse> updateServiceBooking(
            HttpServletRequest request, 
            @PathVariable String bookingId,
            @RequestBody UpdateBookingRequest requestBody) throws Exception {
        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");
		UserIdentity userIdentity = authTokenValidator.checkValid(authToken, appId);
        serviceBookingUtils.validateApplicationId(appId, Arrays.asList(ContinentCode.AP));
        
        ContinentCode continentCode = serviceBookingUtils.getContinentCode(appId);
        String referenceNumber = null;
        if (ContinentCode.AP.equals(continentCode)) {
			requestBody.setBookingId(bookingId);
			requestBody.setAppId(appId);
			referenceNumber = bookingService.updateBooking(
					requestBody, userIdentity, null);
        }
        
        BookingMSLResponse response = new BookingMSLResponse();
    	response.setBookingCustomerRefNum(referenceNumber);
	    response.setLastRequested(ZonedDateTime.now());
        response.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
