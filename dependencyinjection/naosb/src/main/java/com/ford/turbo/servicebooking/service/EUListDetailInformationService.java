package com.ford.turbo.servicebooking.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.input.CountryCode;
import com.ford.turbo.aposb.common.basemodels.input.LanguageCode;
import com.ford.turbo.aposb.common.basemodels.input.RegionCode;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.response.AdditionalService;
import com.ford.turbo.servicebooking.models.msl.response.MainService;
import com.ford.turbo.servicebooking.models.msl.response.OSBOVService;
import com.ford.turbo.servicebooking.models.msl.response.ServicesListResponse;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.eu.web.EUDealerServicesCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;

@Service
public class EUListDetailInformationService implements ListDetailInformationService {
    private static final Logger LOG = LoggerFactory.getLogger(EUListDetailInformationService.class);


    private MutualAuthRestTemplate mutualAuthRestTemplate;
    private TraceInfo traceInfo;
    private CredentialsSource credentialsSource;
  
    @Autowired
    public EUListDetailInformationService(MutualAuthRestTemplate mutualAuthRestTemplate, 
    		TraceInfo traceInfo, 
    		@Qualifier("OSB_DATAPOWER") CredentialsSource credentialsSource) {
        this.credentialsSource = credentialsSource;
        this.traceInfo = traceInfo;
        this.mutualAuthRestTemplate = mutualAuthRestTemplate;

    }

    public ServicesListResponse listServices(LanguageCode language, RegionCode region, CountryCode country,
											 String mileage, String vin, String dealerCode, List<String> voucherCodes) {
		LOG.debug("Listing Services...");

		DealerServicesRequest request = getDealerServicesRequest(language, region, country, mileage, vin, dealerCode,	voucherCodes);
		EUDealerServicesCommand command = getEUDealerServicesCommand(request);
		EUOSBDealerServicesWebResponse osbResponse = command.execute();
		if (osbResponse.getError() != null) {
			FordError error = new FordError();
			error.setMessage(osbResponse.getError().getCode());
			error.setStatusCode(Integer.parseInt(osbResponse.getError().getStatusCode()));
			error.setStatusContext(StatusContext.HTTP.getStatusContext());
			throw new BadRequestException(error);
		}
		ServicesListResponse response = mapOSBServicesToMSLServicesResponse(osbResponse);
		return response;
    }

	protected ServicesListResponse mapOSBServicesToMSLServicesResponse(EUOSBDealerServicesWebResponse osbResponse) {
		ServicesListResponse response = new ServicesListResponse();
		mapOSBMainServicesToMSLMainServicesResponse(response,osbResponse.getData().getMainServices());
		mapOSBAdditionalServicesToMSLAdditionalServicesResponse(response,osbResponse.getData().getAdditionalServices());	
		mapOSBOldServicesToMSLOldServicesResponse(response,osbResponse.getData().getOldServices());
		return response;
	}

    
  	private void mapOSBOldServicesToMSLOldServicesResponse(ServicesListResponse response, List<EUOSBOldServiceResponse> osbOldServices) {
  		if(osbOldServices != null && !osbOldServices.isEmpty()) {
				DecimalFormat decimalFormat = new DecimalFormat();
				decimalFormat.setMaximumFractionDigits(2);
				decimalFormat.setMinimumFractionDigits(2);
				decimalFormat.setGroupingUsed(false);
				
  			osbOldServices.stream().filter((service)->{
  				if(OldServiceType.valueOf(service.getSubType()) != null) {
  					return true;
  				}
  				return false;
  			})
  			.forEach((osbOldService)->{
  				OldServiceType serviceType = OldServiceType.valueOf(osbOldService.getSubType());
  				String serviceId =  osbOldService.getServiceId()+":"+serviceType.name();
  				OSBOVService mslOldService = new OSBOVService();
  				mslOldService.setName(osbOldService.getName());
  				mslOldService.setDescription(osbOldService.getDescription());
  				mslOldService.setServiceId(serviceId);
  				if(osbOldService.getPrice() != null) {
  					mslOldService.setPrice(decimalFormat.format(osbOldService.getPrice()));
  				}
  				if(osbOldService.getPriceAfterDiscount() != null) {
  					mslOldService.setPriceAfterDiscount(decimalFormat.format(osbOldService.getPriceAfterDiscount()));
  				}
  				if(osbOldService.getDiscountPrice() != null) {
  					mslOldService.setDiscountPrice(decimalFormat.format(osbOldService.getDiscountPrice()));
  				}
  				if(osbOldService.getDiscountPercentage() != null) {
  					mslOldService.setDiscountPercentage(String.valueOf(osbOldService.getDiscountPercentage().longValue()));
  				}
  				mslOldService.setSubType(serviceType);
  				response.getOldServices().add(mslOldService);
  				
  				
  			});
		
  		}
		
	}

	private void mapOSBAdditionalServicesToMSLAdditionalServicesResponse(ServicesListResponse response,List<EUOSBAdditionalServiceResponse> osbAdditionalServices) {
  		if(osbAdditionalServices != null && !osbAdditionalServices.isEmpty()) {
  			osbAdditionalServices.stream().forEach((osbAdditionalService)->{
  				AdditionalService mslAdditonalService = new AdditionalService();
  				
  				mslAdditonalService.setName(osbAdditionalService.getName());
  				mslAdditonalService.setDescription(osbAdditionalService.getDescription());
  				mslAdditonalService.setServiceId(osbAdditionalService.getServiceId());
  				mslAdditonalService.setPrice(osbAdditionalService.getPrice());
  				mslAdditonalService.setPriceAfterDiscount(osbAdditionalService.getPriceAfterDiscount());
  				mslAdditonalService.setDiscountPrice(osbAdditionalService.getDiscountPrice());
  				mslAdditonalService.setDiscountPercentage(osbAdditionalService.getDiscountPercentage());
  				response.getAdditional().add(mslAdditonalService);
  			});
  			
  		}
		
	}

	private void mapOSBMainServicesToMSLMainServicesResponse(ServicesListResponse mslResponse,List<EUOSBMainServiceResponse> osbMainServices) {
  		
  		if(osbMainServices != null && !osbMainServices.isEmpty()) {
  			osbMainServices.stream().forEach(mainService ->{
  				MainService mslMainService = new MainService();
  				mslMainService.setName(mainService.getName());
  				mslMainService.setDescription(mainService.getDescription());
  				mslMainService.setServiceId(mainService.getServiceId());
  				mslMainService.setPrice(mainService.getPrice());
  				mslMainService.setPriceAfterDiscount(mainService.getPriceAfterDiscount());
  				mslMainService.setDiscountPrice(mainService.getDiscountPrice());
  				mslMainService.setDiscountPercentage(mainService.getDiscountPercentage());
  				mslMainService.setSubType(mainService.getSubType());
  				mslMainService.setApplicationInformation(mainService.getApplicationInformation());
  				mslResponse.getMain().add(mslMainService);	
  			});	
  		}
		
	}



	protected DealerServicesRequest getDealerServicesRequest(LanguageCode languageCode, RegionCode regionCode, CountryCode countryCode, String mileage, String vin,
			String dealerCode, List<String> voucherCodes) {

		String locale = new StringBuilder().append(languageCode).append("-").append(RegionCode.UK.equals(regionCode) ? "gb" : regionCode.getJsonName()).toString().toLowerCase();
		
		String combinedVoucherCodes = null; 		
		if (voucherCodes != null && !voucherCodes.isEmpty()) {
			combinedVoucherCodes = voucherCodes.stream().filter(e -> StringUtils.isNotEmpty(e)).collect(Collectors.joining(","));
		}
		
		return DealerServicesRequest.builder()
				.dealerCode(dealerCode)
				.marketCode(countryCode.getJsonName())
				.locale(locale)
				.modelName(null)
				.buildYear(null)
				.vin(vin)
				.registrationNumber(null)
				.voucherCode(voucherCodes)
				.combinedVoucherCodes(combinedVoucherCodes)
				.mileage(mileage)
				.build();
	}

	protected EUDealerServicesCommand getEUDealerServicesCommand(DealerServicesRequest request) {

		return new EUDealerServicesCommand(traceInfo, 
									mutualAuthRestTemplate,
									credentialsSource.getBaseUri(), 
									request);
	}
}
