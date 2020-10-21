package servicebooking.controller;

import com.ford.turbo.aposb.common.authsupport.validator.AuthTokenValidator;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.servicebooking.models.msl.response.OsbCompliantDealerResponse;
import com.ford.turbo.servicebooking.models.msl.response.OsbCompliantResponse;
import com.ford.turbo.servicebooking.models.msl.response.OsbCompliantResponseList;
import com.ford.turbo.servicebooking.models.osb.response.dealerdetails.DealerDetailsData;
import com.ford.turbo.servicebooking.models.osb.response.dealerdetails.DealerDetailsResponse;
import com.ford.turbo.servicebooking.service.DealerDetailsService;
import com.ford.turbo.servicebooking.utils.DealerCodeFormatter;
import com.ford.turbo.servicebooking.utils.ServiceBookingUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/v1", produces = "application/json")
public class DealercodeController {

    private final AuthTokenValidator authTokenValidator;
    private final DealerDetailsService dealerDetailsService;
    private final ServiceBookingUtils serviceBookingUtils;
    
    @Autowired
    public DealercodeController(AuthTokenValidator authTokenValidator, DealerDetailsService dealerDetailsService, ServiceBookingUtils serviceBookingUtils) {
        this.authTokenValidator = authTokenValidator;
        this.dealerDetailsService = dealerDetailsService;
        this.serviceBookingUtils = serviceBookingUtils;
    }

    @ApiOperation(
            value = "Validate if given dealer is compliant or not.",
            httpMethod = "POST",
            notes = "[EU] Validate and return the status of compliance for the given dealer."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @PostMapping(value = "/dealercode/validator")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")
    })
    public ResponseEntity<OsbCompliantResponse> getOsbCompliant(
            HttpServletRequest request,
            @ApiParam(value = "Dealer code to be validated", required = true) @RequestParam String dealercode,
            @ApiParam(value = "Market code to be validated", required = true) @RequestParam String marketcode) {

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        
        serviceBookingUtils.validateApplicationId(appId);

        DealerDetailsResponse dealerDetailsResponse = dealerDetailsService.getDealerDetails(DealerCodeFormatter.formatDealerCodes(Arrays.asList(dealercode)), marketcode);

        OsbCompliantResponse osbCompliantResponse = createOsbCompliantResponse(dealerDetailsResponse);

        return new ResponseEntity<>(osbCompliantResponse, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Validate if given dealers are compliant or not.",
            httpMethod = "GET",
            notes = "[EU] Returns compliance status for the provided dealers list."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")})
    @GetMapping(value = "/dealercodes/validator")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "auth-token", paramType = "header", required = true, value = "Authorization token for user", dataType = "String"),
            @ApiImplicitParam(name = "Application-Id", paramType = "header", required = true, value = "Application ID", dataType = "String")
    })
    public ResponseEntity<OsbCompliantResponseList> getOsbCompliantDealers(
            HttpServletRequest request,
            @ApiParam(value = "List of dealer codes to be checked.", required = true) @RequestParam(value = "dealercode") List<String> dealercodes,
            @ApiParam(value = "Market code to be validated", required = true) @RequestParam String marketcode) {

        String authToken = request.getHeader("auth-token");
        String appId = request.getHeader("application-id");

        authTokenValidator.checkValid(authToken, appId);
        
        serviceBookingUtils.validateApplicationId(appId);

        DealerDetailsResponse dealerDetailsResponse = dealerDetailsService.getDealerDetails(DealerCodeFormatter.formatDealerCodes(dealercodes), marketcode);

        return new ResponseEntity<>(createOsbCompliantResponseForMultipleDealers(dealerDetailsResponse), HttpStatus.OK);
    }
    
    private OsbCompliantResponse createOsbCompliantResponse(DealerDetailsResponse response){

        OsbCompliantResponse osbCompliantResponse = new OsbCompliantResponse();
        osbCompliantResponse.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
        osbCompliantResponse.setLastRequested(ZonedDateTime.now());

        if(response.getData() != null && response.getData().length > 0){
            osbCompliantResponse.setCompliant(response.getData()[0].isOsbEnabled());
        }

        return osbCompliantResponse;
    }
    
    private OsbCompliantResponseList createOsbCompliantResponseForMultipleDealers(DealerDetailsResponse osbResponse){
    	List<OsbCompliantDealerResponse> values = new ArrayList<>();
    	OsbCompliantDealerResponse osbCompliantResponse = null;
    	if(osbResponse.getData()!=null)
    	{
    		for(DealerDetailsData dealerData : osbResponse.getData())
    		{
    			osbCompliantResponse = new OsbCompliantDealerResponse();
    			osbCompliantResponse.setCompliant(dealerData.isOsbEnabled());
    			osbCompliantResponse.setDealerCode(dealerData.getDealerCode());
    			values.add(osbCompliantResponse);
    		}
    	}
    	OsbCompliantResponseList list = new OsbCompliantResponseList();
    	list.setRequestStatus(BaseResponse.RequestStatus.CURRENT);
    	list.setLastRequested(ZonedDateTime.now());
    	list.setValues(values);
    	
        return list;
    }
}
