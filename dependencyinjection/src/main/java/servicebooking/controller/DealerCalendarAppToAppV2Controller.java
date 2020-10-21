package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.ApplicationIdValidator;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.servicebooking.models.msl.request.OSBSource;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.models.osb.DealerCalendarV2Response;
import com.ford.turbo.servicebooking.service.DealerCalendarV2Service;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping(value = "/v2/apptoapp", produces = "application/json")
@Slf4j
public class DealerCalendarAppToAppV2Controller {
	private static final String DATE_PATTERN = "yyyyMMdd";
    private final DealerCalendarV2Service dealerCalendarService;
    private final ServiceBookingUtils serviceBookingUtils;
    private final ApplicationIdValidator applicationIdValidator;
    
    @Autowired
    public DealerCalendarAppToAppV2Controller( 
    		DealerCalendarV2Service dealerCalendarService, 
    		ServiceBookingUtils serviceBookingUtils, 
    		ApplicationIdValidator applicationIdValidator) {
        this.dealerCalendarService = dealerCalendarService;
        this.serviceBookingUtils = serviceBookingUtils;
        this.applicationIdValidator = applicationIdValidator;
    }
	
	@ApiOperation(
   		 value = "Retrives Dealer Calendar with available time slots from provided start date.",
            httpMethod = "GET",
            notes = "[AP] Gets time slots that customers can select to book services, "
            		+ "based on provided dealer code and start date."
   )
   @ApiResponses(value = {
           @ApiResponse(code = 200, message = "OK")})
   @ApiImplicitParams(value = {
           @ApiImplicitParam(name = "Authorization", value = "Authorization Bearer token", required = true,
        		   dataType = "String", paramType = "header"), 
           @ApiImplicitParam(name = "Application-Id", value = "Application ID", required = true, 
        		   dataType = "String", paramType = "header") })

   @GetMapping(value = "calendar")
   public ResponseEntity<DealerCalendarV2Response> getCalendar(
           HttpServletRequest request,
           @ApiParam(value = "Start Date (yyyyMMDD)" , required = true) 
           		@RequestParam String startDate,
           @ApiParam(value = "Dealer Code", required = true) 
           		@RequestParam String dealerCode,
       	   @ApiParam(value = "Source e.g. LW_CON, Only for AP", required = true) 
      			@RequestParam(required = true) OSBSource source) {
		
		if(source == null) {
			FordError fordError = new FordError("HTTP", BAD_REQUEST.value(), 
	   				"Required OSBSource parameter 'source' is not present");
	   		throw new BadRequestException(fordError);
		}

	   	if (!serviceBookingUtils.isDateParsable(startDate, DATE_PATTERN)) {
	   		FordError fordError = new FordError("HTTP", BAD_REQUEST.value(), 
	   				"Parameter 'startDate' is expected to be in the following pattern, 'yyyyMMdd'");
	   		throw new BadRequestException(fordError);
	   	}
	   	
		String appId = request.getHeader("Application-Id");

		applicationIdValidator.checkValidRegionalAppId(appId, Arrays.asList(AP));
		log.debug("Successfully validated Application ID: " + appId);
		
		DealerCalendarV2 response = dealerCalendarService.getCalendarWithSource(dealerCode, startDate, 
				source.getJsonName());

		DealerCalendarV2Response dealerResponse = new DealerCalendarV2Response();
		dealerResponse.setLastRequested(ZonedDateTime.now());
		dealerResponse.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
		dealerResponse.setValue(response);
		
		return new ResponseEntity<>(dealerResponse, HttpStatus.OK);
   }
}
