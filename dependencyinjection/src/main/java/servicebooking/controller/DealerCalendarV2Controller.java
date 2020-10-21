package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.models.osb.DealerCalendarV2Response;
import com.ford.turbo.servicebooking.service.DealerCalendarV2Service;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.AP;

@RestController
@RequestMapping(value = "/v2", produces = "application/json")
public class DealerCalendarV2Controller {

	private static final String DATE_PATTERN = "yyyyMMdd";
    private final AuthTokenValidator authTokenValidator;
    private final DealerCalendarV2Service dealerCalendarService;
    private final ServiceBookingUtils serviceBookingUtils;

    @Autowired
    public DealerCalendarV2Controller(AuthTokenValidator authTokenValidator, DealerCalendarV2Service dealerCalendarService, ServiceBookingUtils serviceBookingUtils) {
        this.authTokenValidator = authTokenValidator;
        this.dealerCalendarService = dealerCalendarService;
        this.serviceBookingUtils = serviceBookingUtils;
    }

    @ApiOperation(
    		 value = "Retrives Dealer Calendar with available time slots from provided start date.",
             httpMethod = "GET",
             notes = "[AP] Gets time slots that customers can select to book services, based on provided dealer code and start date."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })

    @GetMapping(value = "calendar")
    public ResponseEntity<DealerCalendarV2Response> getCalendar(
            HttpServletRequest request,
            @ApiParam(value = "Start Date" , required = true) @RequestParam String startDate,
            @ApiParam(value = "Dealer Code", required = true) @RequestParam String dealerCode) {

    	if (!serviceBookingUtils.isDateParsable(startDate, DATE_PATTERN)) {
    		FordError fordError = new FordError("HTTP", 400, "Parameter 'startDate' is expected to be in the following pattern, 'yyyyMMdd'");
    		throw new BadRequestException(fordError);
    	}
        ZonedDateTime lastRequested = ZonedDateTime.now();
        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        
        serviceBookingUtils.validateApplicationId(appId, Arrays.asList(AP));
        
        DealerCalendarV2 response = dealerCalendarService.getCalendar(dealerCode, startDate, appId);
        
        DealerCalendarV2Response dealerResponse = new DealerCalendarV2Response();
        dealerResponse.setLastRequested(lastRequested);
        dealerResponse.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        dealerResponse.setValue(response);
        return new ResponseEntity<>(dealerResponse, HttpStatus.OK);
    }
}
