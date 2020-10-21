package com.ford.turbo.servicebooking.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.exception.BookingAlreadyExistsException;
import com.ford.turbo.servicebooking.exception.InvalidBookingNumberException;
import com.ford.turbo.servicebooking.exception.ServiceNotSupportedException;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingRequest;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.msl.response.v2.BookingDetailsServiceResponseValue;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.CancelBookedServiceResponse;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.DealerCodeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.CancelBookingCommand;
import com.ford.turbo.servicebooking.command.GetBookingsCommand;
import com.ford.turbo.servicebooking.command.eu.EUCreateBookingCommand;
import com.ford.turbo.servicebooking.models.osb.OSBBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingData;
import com.ford.turbo.servicebooking.models.osb.OSBCCancelBookingResponse;
import com.ford.turbo.servicebooking.models.osb.RetrieveBookingsForOwnerResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;
import com.ford.turbo.servicebooking.models.osb.request.EUOSBCustomer;
import com.ford.turbo.servicebooking.models.osb.request.OSBCreateBookingAdditionalService;
import com.ford.turbo.servicebooking.utils.BookedServiceResponseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EUBookingService implements BookingService{

    private static final String GUID = "00000";

    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private TraceInfo traceInfo;
    private CredentialsSource credentialsSource;
    private final UserProfileService userProfileService;

    @Autowired
    public EUBookingService(MutualAuthRestTemplate mutualAuthRestTemplate, TraceInfo traceInfo, @Qualifier("OSB_DATAPOWER") CredentialsSource credentialsSource, UserProfileService userProfileService) {
        this.mutualAuthRestTemplate = mutualAuthRestTemplate;
        this.traceInfo = traceInfo;
        this.credentialsSource = credentialsSource;
        this.userProfileService = userProfileService;
        
    }

    @Override
    public BookedServiceResponse listServiceBookings(String opusConsumerId, String marketCode, Optional<String> vin) {
    	if (!vin.isPresent()) {
            log.debug("Listing Service Bookings (no vin)...");
    	}
        log.debug("Listing Service Bookings...");
        GetBookingsCommand command = new GetBookingsCommand(traceInfo, mutualAuthRestTemplate, marketCode, credentialsSource.getBaseUri(), opusConsumerId);
        RetrieveBookingsForOwnerResponse response = command.execute();

        OSBBookingData[] bookingData = response.getData();

        if (vin.isPresent()) {
            return BookedServiceResponseMapper.map(Arrays.stream(bookingData)
                            .filter(booking -> vin.get().equalsIgnoreCase(booking.getVin()))
                            .collect(Collectors.toList())
                            .toArray(new OSBBookingData[]{}));
        }

        return BookedServiceResponseMapper.map(bookingData);
    }

    @Override
    public CancelBookedServiceResponse cancelBooking(String bookingRefNumber, String marketCode, String appId, String authToken) {
		if(StringUtils.isBlank(marketCode)) {
			throw new BadRequestException(new Exception("Missing mandatory parameter marketCode"), "MSL");
		}
		UserProfile userProfile = userProfileService.getUserProfile(authToken, appId);
		validateUser(userProfile.getUserId(), marketCode, bookingRefNumber);
		
        OSBCCancelBookingResponse osbcCancelBookingResponse = getCancelBookingCommand(bookingRefNumber).execute();
        final OSBCCancelBookingData data = osbcCancelBookingResponse.getData();

        boolean cancelled = (data != null) && (data.getIsBookingCancelled() != null) && data.getIsBookingCancelled();
        CancelBookedServiceResponse response = new CancelBookedServiceResponse();
        response.setCancelled(cancelled);
        return response;
    }

	CancelBookingCommand getCancelBookingCommand(String bookingRefNumber) {
		CancelBookingCommand command = new CancelBookingCommand(traceInfo, mutualAuthRestTemplate, bookingRefNumber, credentialsSource.getBaseUri());
		return command;
	}

	@Override
	public BookingDetailsServiceResponseValue getBookingDetails(String bookingId, String appId){
		throw new ServiceNotSupportedException("Only for AP!!getBookingDetails not supported.");
	}

	@Override
	public String createBooking(CreateBookingRequest requestBody, String appId, String authToken) throws Exception {

		UserProfile userProfile = userProfileService.getUserProfile(authToken, appId);
		requestBody.setDealerCode(DealerCodeFormatter.formatDealerCode(requestBody.getDealerCode()));
		return this.createBooking(requestBody, userProfile);
	}
	
    private String createBooking(CreateBookingRequest requestBody, UserProfile userProfile) throws Exception {

        CreateBookingOSBRequest osbRequest = getCreateBookingOSBRequest(requestBody, userProfile);
      
        EUCreateBookingCommand command = createEUCreateBookingCommand(osbRequest);
        EUOSBCreateBookingResponse response = command.execute();
		if (response.getError() != null) {
			if(response.getError().getCode() != null && response.getError().getCode().trim().equalsIgnoreCase("OSB_VIN_EXISTS")) {
				 throw new BookingAlreadyExistsException(requestBody.getVin());
			} else {
				FordError error = new FordError(StatusContext.HTTP.getStatusContext(), Integer.parseInt(response.getError().getStatusCode()), response.getError().getCode());
				throw new BadRequestException(error);
			}
		}
        return response.getData().getBookingReferenceNumber();
    }

	protected CreateBookingOSBRequest getCreateBookingOSBRequest(CreateBookingRequest requestBody,
			UserProfile userProfile) {
		
		String mainServiceId = null;
		if (StringUtils.isNotBlank(requestBody.getMainServiceId())) {
			mainServiceId = requestBody.getMainServiceId();
		} else {
			mainServiceId = "66666";
		}

		String locale = new StringBuilder().append(requestBody.getLanguage()).append("-")
				.append(RegionCode.UK.equals(requestBody.getRegion()) ? "gb" : requestBody.getRegion()).toString()
				.toLowerCase();

		DateTimeFormatter appointmentTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"); // ("dd-MM-yyyy'T'HH:mm:ss");  25-06-2018T16:30:00",
		String appointmentTime = requestBody.getApptTime().format(appointmentTimeFormatter);

		
		List<String> voucherCodes = new ArrayList<>();
		if(requestBody.getVoucherCode() != null && !requestBody.getVoucherCode().trim().isEmpty()) {
			voucherCodes.add(requestBody.getVoucherCode().trim());
		}
	
		return CreateBookingOSBRequest.builder()
				.locale(locale)
				.marketCode(requestBody.getCountry().getJsonName())
				.dealerCode(requestBody.getDealerCode())
				.vin(requestBody.getVin())
				.osbSiteTermsRequired(true)
				.mileage(requestBody.getMileage())
				.voucherCodes(voucherCodes)
				.serviceType("50")
				.comments(requestBody.getCustomerAnnotation())
				.appointmentTime(appointmentTime)
				.mainServiceId(mainServiceId)
				.valueServiceId(filterOldService(requestBody.getOldServices(), OldServiceType.VALUE))
				.motServiceId(filterOldService(requestBody.getOldServices(), OldServiceType.MOT))
				.repairServices(filterOldServices(requestBody.getOldServices(), OldServiceType.REPAIR))
				.customer(getEUOSBCustomer(userProfile))
				.newAdditionalServices(getOSBAdditionalServices(requestBody))
				.build();
	}

	private List<OSBCreateBookingAdditionalService> getOSBAdditionalServices(CreateBookingRequest requestBody) {
		if(requestBody.getAdditionalServices() != null) {
			return requestBody.getAdditionalServices().stream()
					.map(additionalService -> {
						OSBCreateBookingAdditionalService osbAdditionalService = OSBCreateBookingAdditionalService.builder()
								.additionalServiceId(additionalService.getAdditionalServiceId())
								.additionalServiceComments(additionalService.getAdditionalServiceComments())
								.build();
						return osbAdditionalService;
					})
					.collect(Collectors.toList());
		}
		return new ArrayList<OSBCreateBookingAdditionalService>();
	}

	private EUOSBCustomer getEUOSBCustomer(UserProfile userProfile) {
		if(userProfile != null) {
			return EUOSBCustomer.builder()
			  .firstName(userProfile.getFirstName())
			  .lastName(userProfile.getLastName())
			  .email(userProfile.getUserId())
			  .title(userProfile.getTitle())
			  .contactNumber(userProfile.getPhoneNumber())
			  .guid(GUID)
			  .build();
		}
		return null;
	}

	private List<String> filterOldServices(List<String> oldServiceIds, OldServiceType serviceType) {
		List<String> oldServices = new ArrayList<>();
		if (oldServiceIds != null) {
			for (String oldServiceId : oldServiceIds) {
				if (oldServiceId.split(":")[1].equals(serviceType.toString())) {
					oldServices.add(oldServiceId.split(":")[0]);
				}
			}

		}
		return oldServices;
	}
	
	private String filterOldService(List<String> oldServiceIds, OldServiceType serviceType) {
		
		if (oldServiceIds != null) {
			for (String oldServiceId : oldServiceIds) {
				if (oldServiceId.split(":")[1].equals(serviceType.toString())) {
					return (oldServiceId.split(":")[0]);
				}
			}

		}
		return null;
	}
    protected EUCreateBookingCommand createEUCreateBookingCommand(CreateBookingOSBRequest request) {
    	
    		return new EUCreateBookingCommand(traceInfo, mutualAuthRestTemplate, credentialsSource.getBaseUri(), request);
    }
    
    private void validateUser(String opusConsumerId, String marketCode, String referenceNumber) {

        List<BookedServiceResponse> bookedServices = this.listServiceBookingsForDelete(opusConsumerId, marketCode);
        
        if (bookedServices.stream().filter(v-> v.getBookingCustomerRefNum().equals(referenceNumber)).findFirst().isPresent()) {
            return;
        }
        throw new InvalidBookingNumberException();
    }

	public List<BookedServiceResponse> listServiceBookingsForDelete(String opusConsumerId, String marketCode) {

        GetBookingsCommand command = new GetBookingsCommand(traceInfo, mutualAuthRestTemplate, marketCode, credentialsSource.getBaseUri(), opusConsumerId);
        RetrieveBookingsForOwnerResponse response = command.execute();

        OSBBookingData[] bookingData = response.getData();

        return BookedServiceResponseMapper.mapBookedServices(bookingData);
	}

}