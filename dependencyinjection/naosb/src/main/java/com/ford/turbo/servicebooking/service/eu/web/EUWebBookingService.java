
package com.ford.turbo.servicebooking.service.eu.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.WebBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.eu.EUCreateBookingCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUAccessCodesNotificationCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUCancelBookingCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUGetBookingsCommand;
import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.CustomerWeb;
import com.ford.turbo.servicebooking.models.eu.web.DealerProfile;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWeb;
import com.ford.turbo.servicebooking.models.msl.request.AccessCodesNotificationRequest;
import com.ford.turbo.servicebooking.models.msl.request.CancelBookingRequest;
import com.ford.turbo.servicebooking.models.msl.request.CreateBookingWebRequest;
import com.ford.turbo.servicebooking.models.msl.request.EUWebCustomer;
import com.ford.turbo.servicebooking.models.msl.request.GetBookingsRequest;
import com.ford.turbo.servicebooking.models.msl.response.AccessCodesNotificationWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.CancelBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.CreateBookingWebWrapper;
import com.ford.turbo.servicebooking.models.msl.response.GetBookingsData;
import com.ford.turbo.servicebooking.models.msl.response.GetBookingsWebWrapper;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAccessCodesNotificationResponseData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBBookedServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCancelBookingResponseData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCreateBookingResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBCustomerResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerProfileResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBGetBookingsData;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBServiceVoucher;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleDetailsResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBWebError;
import com.ford.turbo.servicebooking.models.osb.request.CreateBookingOSBRequest;
import com.ford.turbo.servicebooking.models.osb.request.EUOSBCustomer;
import com.ford.turbo.servicebooking.models.osb.request.OSBCreateBookingAdditionalService;

@Service
public class EUWebBookingService implements WebBookingService {

	private final String EU_OSB_STATUS_CONTEXT = "EU OSB";
	private TraceInfo traceInfo;
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String baseUrl;
	
	@Autowired
	public EUWebBookingService(TraceInfo traceInfo, MutualAuthRestTemplate mutualAuthRestTemplate,
			@Qualifier("OSB_DATAPOWER") CredentialsSource euOsbCredentialsSource) {
		this.traceInfo = traceInfo;
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = euOsbCredentialsSource.getBaseUri();
	}
	
	@Override
	public CancelBookingWebWrapper cancelBooking(String bookingReferenceNumber, String accessCode,
												 boolean osbSiteTermsRequired) {
		
		CancelBookingRequest request = createCancelBookingRequest(bookingReferenceNumber, accessCode,
				osbSiteTermsRequired);
		EUCancelBookingCommand command = getEUCancelBookingCommand(request);
		EUOSBCancelBookingResponse response = command.execute();
		checkOSBErrorNotNull_andThrowMSLException(response.getError());
		return mapResponseData_toWebWrapper(response.getData());
	}

	@Override
	public AccessCodesNotificationWebWrapper sendAccessCodesNotification(AccessCodesNotificationRequest request) {

		EUOSBAccessCodesNotificationResponse response = getEUAccessCodeNotificationCommand(request).execute();
		checkOSBErrorNotNull_andThrowMSLException(response.getError());
		return mapResponseData_toWebWrapper(response.getData());
	}
	
	@Override
	public GetBookingsWebWrapper getBookings(String accessCode, String email) {
		GetBookingsRequest request = createGetBookingsRequest(email, accessCode);
		EUGetBookingsCommand command = getEUGetBookingsCommand(request);
		
		EUOSBBookedServicesResponse response = command.execute();
		checkOSBErrorNotNull_andThrowMSLException(response.getError());
		
		return convertGetBookingsToMSLResponse(response.getData());
	}

	@Override
	public CreateBookingWebWrapper createBooking(CreateBookingWebRequest webRequest) {
		
		CreateBookingOSBRequest osbRequest = mapWebRequestToOSBRequest(webRequest);
		EUCreateBookingCommand command = getEUCreateBookingCommand(osbRequest);
		EUOSBCreateBookingResponse response = command.execute();
		checkOSBErrorNotNull_andThrowMSLException(response.getError());
		return response.getData();
	}
	
	protected CreateBookingOSBRequest mapWebRequestToOSBRequest(CreateBookingWebRequest webRequest) {
		
		Function<CreateBookingWebRequest, CreateBookingOSBRequest> mapFunction = (request) -> {
			CreateBookingOSBRequest osbRequest = CreateBookingOSBRequest.builder()
					.locale(request.getLocale())
					.marketCode(request.getMarketCode())
					.dealerCode(request.getDealerCode())
					.modelName(request.getModelName())
					.buildYear(request.getBuildYear())
					.vin(request.getVin())
					.registrationNumber(request.getRegistrationNumber())
					.osbSiteTermsRequired(request.getOsbSiteTermsRequired())
					.voucherCodes(request.getVoucherCodes())
					.serviceType(request.getServiceType())
					.comments(request.getComments())
					.appointmentTime(request.getAppointmentTime())
					.mainServiceId(request.getMainServiceId())
					.valueServiceId(request.getValueServiceId())
					.motServiceId(request.getMotServiceId())
					.repairServices(request.getRepairServices())
					.customer(mapCustomer(request.getCustomer()))
					.newAdditionalServices(mapAdditionalServices(request.getAdditionalServices()))
					.build();
			return osbRequest;
		};
		CreateBookingOSBRequest osbRequest = mapFunction.apply(webRequest);
		return osbRequest;
	}

	private EUOSBCustomer mapCustomer(EUWebCustomer webCustomer) {
		
		if (webCustomer == null) {
			return null;
		}

		return EUOSBCustomer.builder()
				.firstName(webCustomer.getFirstName())
				.lastName(webCustomer.getLastName())
				.title(webCustomer.getTitle())
				.email(webCustomer.getEmail())
				.contactNumber(webCustomer.getContactNumber())
				.build();
	}
	
	protected List<OSBCreateBookingAdditionalService> mapAdditionalServices (List<String> webAdditionalServices) {
		List<OSBCreateBookingAdditionalService> osbAdditionalServices = new ArrayList<>();
		if (webAdditionalServices != null && !webAdditionalServices.isEmpty()) {
		osbAdditionalServices = webAdditionalServices.stream()
				.map(webAdditionalService -> {
					OSBCreateBookingAdditionalService osbAdditionalService = OSBCreateBookingAdditionalService.builder()
							.additionalServiceId(webAdditionalService)
							.build();
					return osbAdditionalService;
				})
				.collect(Collectors.toList());
		}
		return osbAdditionalServices;
	}

	protected void checkOSBErrorNotNull_andThrowMSLException(EUOSBWebError osbError) {
		if (osbError != null) {
			FordError error = new FordError(EU_OSB_STATUS_CONTEXT, Integer.parseInt(osbError.getStatusCode()), osbError.getCode());
			throw new BadRequestException(error);
		}
	}

	protected CancelBookingWebWrapper mapResponseData_toWebWrapper(EUOSBCancelBookingResponseData responseData) {
		
		boolean isBookingCancelled = responseData.getIsBookingCancelled();
		return CancelBookingWebWrapper.builder().bookingCancelled(isBookingCancelled).build();
	}

	protected AccessCodesNotificationWebWrapper mapResponseData_toWebWrapper(EUOSBAccessCodesNotificationResponseData responseData) {
		
		boolean notified = responseData.getIsReminderSent();
		AccessCodesNotificationWebWrapper wrapper= AccessCodesNotificationWebWrapper.builder().notified(notified).build();
		return wrapper;
	}

	protected CancelBookingRequest createCancelBookingRequest(String bookingReferenceNumber, String accessCode,
			boolean osbSiteTermsRequired) {
		
		return CancelBookingRequest.builder()
				.bookingReferenceNumber(bookingReferenceNumber)
				.accessCode(accessCode)
				.osbSiteTermsRequired(osbSiteTermsRequired)
				.build();
	}

	protected EUCancelBookingCommand getEUCancelBookingCommand(CancelBookingRequest request) {
		
		return new EUCancelBookingCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);		
	}

	protected EUAccessCodesNotificationCommand getEUAccessCodeNotificationCommand(AccessCodesNotificationRequest request) {
		
		return new EUAccessCodesNotificationCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);		
	}

	protected GetBookingsRequest createGetBookingsRequest(String email, String accessCode) {
		
		return GetBookingsRequest.builder()
				.email(email)
				.accessCode(accessCode)
				.build();
	}
	
	protected EUCreateBookingCommand getEUCreateBookingCommand(CreateBookingOSBRequest request) {
		
		return new EUCreateBookingCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}

	protected EUGetBookingsCommand getEUGetBookingsCommand(GetBookingsRequest request) {
		return new EUGetBookingsCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}


	protected GetBookingsWebWrapper convertGetBookingsToMSLResponse(EUOSBGetBookingsData data) {
		if(data == null)
			return null;
			
		GetBookingsData booking = GetBookingsData.builder()
											.appointmentTime(data.getAppointmentTime())
											.dealer(convertToDealerProfile(data.getDealerProfile()))
											.previousBooking(data.isPreviousBooking())
											.bookingReferenceNumber(data.getBookingReferenceNumber())
											.comments(data.getComments())
											.oldServices(convertToOldServicesWeb(data.getOldServices()))
											.additionalServices(convertToAdditionalServicesWeb(data.getAdditionalServices()))
											.vehicleDetails(convertToVehicleDetailsWeb(data.getVehicleDetails()))
											.mainService(convertToMainServicesWeb(data.getMainService()))
											.voucherCodes(convertToServiceVoucher(data.getVoucherCodes()))
											.vehicleLineDescription(data.getVehicleLineDescription())
											.customer(convertToCustomerWeb(data.getCustomer()))
										.build();
		
		GetBookingsWebWrapper wrapper = GetBookingsWebWrapper.builder()
										.bookings(Arrays.asList(booking))
										.build();
		return wrapper;
		
	}

	protected CustomerWeb convertToCustomerWeb(EUOSBCustomerResponse euCustomer) {
		if (euCustomer == null)
			return null;
		
		CustomerWeb customer = CustomerWeb.builder()
									.lastName(euCustomer.getLastName())
									.title(euCustomer.getTitle())
									.contactNumber(euCustomer.getContactNumber())
									.firstName(euCustomer.getFirstName())
									.email(euCustomer.getEmail())
									.phone(euCustomer.getPhone())
									.build();

		return customer;
	}

	protected List<ServiceVoucher> convertToServiceVoucher(List<EUOSBServiceVoucher> euVoucherCodes) {
		if (euVoucherCodes == null)
			return null;

		List<ServiceVoucher> serviceVouchers = null;
		serviceVouchers = euVoucherCodes.stream().map(euVoucherCode -> {
			ServiceVoucher serviceVoucher = ServiceVoucher.builder()
					.description(euVoucherCode.getVoucherCodeDescription())
					.amount(euVoucherCode.getVoucherAmount())
					.percentage(euVoucherCode.getVoucherPercentage())
					.code(euVoucherCode.getVoucherCode())
					.build();
			return serviceVoucher;
		}).collect(Collectors.toList());
		
		return serviceVouchers;
	}

	protected MainServicesWeb convertToMainServicesWeb(EUOSBMainServiceResponse euMainService) {
		if (euMainService == null)
			return null;

		MainServicesWeb mainServicesWeb = MainServicesWeb.builder()
				.serviceId(euMainService.getServiceId())
				.priceAfterDiscount(euMainService.getPriceAfterDiscount())
				.discountPrice(euMainService.getDiscountPrice())
				.price(euMainService.getPrice())
				.discountPercentage(euMainService.getDiscountPercentage())
				.subType(euMainService.getSubType())
				.name(euMainService.getName())
				.description(euMainService.getDescription())
				.applicationInformation(euMainService.getApplicationInformation())
				.build();
		
		return mainServicesWeb;
	}

	protected VehicleDetailsWeb convertToVehicleDetailsWeb(EUOSBVehicleDetailsResponse euVehicleDetails) {
		if (euVehicleDetails == null)
			return null;

		VehicleDetailsWeb vehicleDetailsWeb = VehicleDetailsWeb.builder()
				.engine(euVehicleDetails.getEngine())
				.registrationNumber(euVehicleDetails.getRegistrationNumber())
				.color(euVehicleDetails.getColor())
				.transmission(euVehicleDetails.getTransmission())
				.vehicleLineCode(euVehicleDetails.getVehicleLineCode())
				.mileageInMiles(euVehicleDetails.getMileageInMiles())
				.bodyStyle(euVehicleDetails.getBodyStyle())
				.fuelType(euVehicleDetails.getFuelType())
				.mileageInKm(euVehicleDetails.getMileageInKm())
				.modelName(euVehicleDetails.getModelName())
				.version(euVehicleDetails.getVersion())
				.vin(euVehicleDetails.getVin())
				.buildDate(euVehicleDetails.getBuildDate())
				.transmissionType(euVehicleDetails.getTransmissionType())
				.build();
		
		return vehicleDetailsWeb;
	}

	protected List<AdditionalServicesWeb> convertToAdditionalServicesWeb(
			List<EUOSBAdditionalServiceResponse> euAdditionalServices) {
		if (euAdditionalServices == null)
			return null;

		List<AdditionalServicesWeb> additionalServices = null;
		additionalServices = euAdditionalServices
				.stream().map(additionalService -> {
					AdditionalServicesWeb additionalServiceWeb = AdditionalServicesWeb.builder()
							.serviceId(additionalService.getServiceId())
							.priceAfterDiscount(additionalService.getPriceAfterDiscount())
							.discountPrice(additionalService.getDiscountPrice())
							.price(additionalService.getPrice())
							.discountPercentage(additionalService.getDiscountPercentage())
							.selected(additionalService.isSelected())
							.name(additionalService.getName())
							.description(additionalService.getDescription())
							.build();

			return additionalServiceWeb;
		}).collect(Collectors.toList());

		return additionalServices;
	}

	protected List<OldServicesWeb> convertToOldServicesWeb(List<EUOSBOldServiceResponse> euOldServices) {
		if (euOldServices == null)
			return null;

		List<OldServicesWeb> oldServices = null;
		oldServices = euOldServices
						.stream()
						.map(oldService -> {
								OldServicesWeb oldServiceWeb = OldServicesWeb.builder()
										.serviceId(oldService.getServiceId())
										.priceAfterDiscount(oldService.getPriceAfterDiscount())
										.discountPrice(oldService.getDiscountPrice())
										.price(oldService.getPrice()).subType(oldService.getSubType())
										.discountPercentage(oldService.getDiscountPercentage())
										.name(oldService.getName())
										.description(oldService.getDescription())
										.build();
		
								return oldServiceWeb;
						}).collect(Collectors.toList());
		
		return oldServices;
	}

	protected DealerProfile convertToDealerProfile(EUOSBDealerProfileResponse euosbDealerProfileResponse) {
		if (euosbDealerProfileResponse == null)
			return null;

		DealerProfile dealerProfile = DealerProfile.builder()
							.street(euosbDealerProfileResponse.getStreet())
							.dealerName(euosbDealerProfileResponse.getDealerName())
							.dealerCode(euosbDealerProfileResponse.getDealerCode())
							.district(euosbDealerProfileResponse.getDistrict())
							.phone(euosbDealerProfileResponse.getPhone())
							.town(euosbDealerProfileResponse.getTown())
							.openingHours(euosbDealerProfileResponse.getOpeningHours())
							.country(euosbDealerProfileResponse.getCountry())
							.postalCode(euosbDealerProfileResponse.getPostalCode())
							.email(euosbDealerProfileResponse.getEmail())
							.build();
		
		return dealerProfile;
	}
}
