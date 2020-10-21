package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarResponse;
import com.ford.turbo.servicebooking.service.DealerCalendarService;
import com.ford.turbo.servicebooking.utils.DealerCodeFormatter;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1", produces = "application/json")
public class DealerCalendarController {

    private final AuthTokenValidator authTokenValidator;
    private final DealerCalendarService dealerCalendarService;
    private final ServiceBookingUtils serviceBookingUtils;

    @Autowired
    public DealerCalendarController(AuthTokenValidator authTokenValidator, DealerCalendarService dealerCalendarService, ServiceBookingUtils serviceBookingUtils) {
        this.authTokenValidator = authTokenValidator;
        this.dealerCalendarService = dealerCalendarService;
        this.serviceBookingUtils = serviceBookingUtils;
    }

    @ApiOperation(
            value = "Retrives Dealer Calendar with available time slots.",
            httpMethod = "GET",
            notes = "[EU] Gets time slots that customers can select to booking services, based on provided dealer code and marketCode."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String") })

    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public ResponseEntity<DealerCalendarResponse> getCalendar(
            HttpServletRequest request,
            @ApiParam(value = "Dealer Code", required = true) @RequestParam String dealerCode,
            @ApiParam(value = "2-3 character market code", required = true) @RequestParam String marketCode,
            @ApiParam(value = "Additional service ids", required = false) @RequestParam(value = "additionalServiceId", required = false, defaultValue = "") List<String> additionalServiceId) {

        ZonedDateTime lastRequested = ZonedDateTime.now();

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        
        serviceBookingUtils.validateApplicationId(appId);

        DealerCalendarResponse response = dealerCalendarService.getCalendar(DealerCodeFormatter.formatDealerCode(dealerCode), marketCode, additionalServiceId);
        response.setLastRequested(lastRequested);
        response.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
