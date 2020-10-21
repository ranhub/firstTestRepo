package com.ford.turbo.servicebooking.service.eu.web;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleDetails;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWrapper;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleLookupRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.WebServiceBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.eu.web.EUWebVehicleLookupCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleLookupResponse;

@Service
public class EUWebServiceBookingService implements WebServiceBookingService {
	
	private final String EU_OSB_STATUS_CONTEXT = "EU OSB";
	
	private final MutualAuthRestTemplate restTemplate;
	private TraceInfo traceInfo;
	private CredentialsSource euOsbCredentialsSource;

	@Autowired
	public EUWebServiceBookingService(MutualAuthRestTemplate restTemplate, TraceInfo traceInfo,
			@Qualifier("OSB_DATAPOWER") CredentialsSource euOsbCredentialsSource) {
		this.restTemplate = restTemplate;
		this.traceInfo = traceInfo;
		this.euOsbCredentialsSource = euOsbCredentialsSource;

	}

	@Override
	public VehicleDetailsWrapper getVehicleLookup(String vin, String registrationNumber, String locale, String marketCode,
												  long mileage, String ecatMarketCode, boolean osbSiteTermsRequired) throws Exception {
		
		EUWebVehicleLookupRequest vehicleLookupRequest = buildVehicleRequest(vin, registrationNumber, locale, marketCode,
				mileage, ecatMarketCode, osbSiteTermsRequired);
		EUOSBVehicleLookupResponse euOSBVehicleLookupResponse = getEUVehicleLookupCommand(vehicleLookupRequest).execute();
		
		if (euOSBVehicleLookupResponse.getError() != null) {
			FordError error = new FordError();
			error.setMessage(euOSBVehicleLookupResponse.getError().getCode());
			error.setStatusCode(Integer.parseInt(euOSBVehicleLookupResponse.getError().getStatusCode()));
			error.setStatusContext(EU_OSB_STATUS_CONTEXT);
			throw new BadRequestException(error);
		}
		
		EUWebVehicleDetails vehicleDetails = euOSBVehicleLookupResponse.getVehicleDetails();

		return new VehicleDetailsWrapper(vehicleDetails);
	}

	protected EUWebVehicleLookupRequest buildVehicleRequest(String vin, String registrationNumber, String locale,
			String marketCode, long mileage, String ecatMarketCode, boolean osbSiteTermsRequired) {
			EUWebVehicleLookupRequest vehicleLookupRequest = 
					EUWebVehicleLookupRequest.builder()
											.vin(vin)
											.registrationNumber(registrationNumber)
											.locale(locale).marketCode(marketCode)
											.mileage(mileage)
											.ecatMarketCode(ecatMarketCode)
											.osbSiteTermsRequired(osbSiteTermsRequired)
											.build();
		return vehicleLookupRequest;
	}

	protected EUWebVehicleLookupCommand getEUVehicleLookupCommand(EUWebVehicleLookupRequest vehicleLookupRequest) {
		return new EUWebVehicleLookupCommand(traceInfo, restTemplate,euOsbCredentialsSource.getBaseUri(), vehicleLookupRequest);
	}
}
