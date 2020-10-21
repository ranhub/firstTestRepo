package com.ford.turbo.servicebooking.service.eu.web;

import java.util.List;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerServicesRequest;
import com.ford.turbo.servicebooking.models.msl.request.EUDealersRequest;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWeb;
import com.ford.turbo.servicebooking.models.msl.response.DealerServicesWebWrapper;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.service.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ford.turbo.servicebooking.command.eu.web.EUDealerServicesCommand;
import com.ford.turbo.servicebooking.command.eu.web.EUDealersCommand;
import com.ford.turbo.servicebooking.models.eu.web.AdditionalServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.Dealer;
import com.ford.turbo.servicebooking.models.eu.web.MainServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.OldServicesWeb;
import com.ford.turbo.servicebooking.models.eu.web.ServiceVoucher;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBAdditionalServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerServicesWebResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealersResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBMainServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBOldServiceResponse;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBServiceVoucher;

@Service
public class EUDealerService implements DealerService {
	
	private final String EU_OSB_STATUS_CONTEXT = "EU OSB";
	
	private TraceInfo traceInfo;
	private MutualAuthRestTemplate mutualAuthRestTemplate;
	private String baseUrl;

	@Autowired
	public EUDealerService(TraceInfo traceInfo, MutualAuthRestTemplate mutualAuthRestTemplate,
			@Qualifier("OSB_DATAPOWER") CredentialsSource euOsbCredentialsSource) {
		this.traceInfo = traceInfo;
		this.mutualAuthRestTemplate = mutualAuthRestTemplate;
		this.baseUrl = euOsbCredentialsSource.getBaseUri();
	}
	
	public EUDealerService() {
		
	}

	public List<Dealer> getDealersbyMarketCode(String marketCode) {
		EUDealersRequest request = EUDealersRequest.builder().marketCode(marketCode).build();
		EUDealersCommand command = getEUDealersCommand(request);
		EUOSBDealersResponse commandResponse = command.execute();
		if (commandResponse.getError() != null) {
			FordError error = new FordError(EU_OSB_STATUS_CONTEXT, Integer.parseInt(commandResponse.getError().getStatusCode()), commandResponse.getError().getCode());
			throw new BadRequestException(error);
		}
		return commandResponse.getData();
	}
	
	@Override
	public DealerServicesWebWrapper getDealerServices(DealerServicesRequest request) {
		request.setCombinedVoucherCodes(getCombinedVoucherCodes(request.getVoucherCode()));
		EUDealerServicesCommand command = getDealerServicesCommand(request);

		EUOSBDealerServicesWebResponse commandResponse = command.execute();

		if (commandResponse.getError() != null) {
			FordError error = new FordError(EU_OSB_STATUS_CONTEXT,
					Integer.parseInt(commandResponse.getError().getStatusCode()), commandResponse.getError().getCode());
			throw new BadRequestException(error);
		}

		return convertToMSLResponse(commandResponse.getData());
	}
	
	public EUDealersCommand getEUDealersCommand(EUDealersRequest request) {
		return new EUDealersCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}
	
	public EUDealerServicesCommand getDealerServicesCommand(DealerServicesRequest request) {
		return new EUDealerServicesCommand(traceInfo, mutualAuthRestTemplate, baseUrl, request);
	}
	
	protected String getCombinedVoucherCodes(List<String> voucherCodes) {
		String combinedVoucherCodes = null;

		if (!CollectionUtils.isEmpty(voucherCodes)) {
			combinedVoucherCodes = voucherCodes
					.stream()
					.filter(e -> !StringUtils.isEmpty(e))
					.collect(Collectors.joining(","));
		}

		return combinedVoucherCodes;
	}
	
	protected DealerServicesWebWrapper convertToMSLResponse(EUOSBDealerServicesResponse euOSBResponse) {
		if(euOSBResponse == null)
			return null;
		
		DealerServicesWeb dealerServicesWeb = DealerServicesWeb.builder()
				.mainServices(convertToMainServicesWeb(euOSBResponse.getMainServices()))
				.additionalServices(convertToAdditionalServicesWeb(euOSBResponse.getAdditionalServices()))
				.oldServices(convertToOldServicesWeb(euOSBResponse.getOldServices()))
				.serviceVouchers(convertToServiceVouchers(euOSBResponse.getVoucherCodes()))
				.build();
		
		return DealerServicesWebWrapper.builder()
					.dealerServices(dealerServicesWeb)
				.build();
	}
	
	protected List<MainServicesWeb> convertToMainServicesWeb(List<EUOSBMainServiceResponse> euOSBMainServices) {
		List<MainServicesWeb> mainServices = null;
		if(euOSBMainServices != null) {
			mainServices = euOSBMainServices
				.stream()
				.map(mainService -> {
					MainServicesWeb mainServiceWeb = MainServicesWeb.builder()
							.serviceId(mainService.getServiceId())
							.priceAfterDiscount(mainService.getPriceAfterDiscount())
							.discountPrice(mainService.getDiscountPrice())
							.price(mainService.getPrice())
							.discountPercentage(mainService.getDiscountPercentage())
							.subType(mainService.getSubType())
							.name(mainService.getName())
							.description(mainService.getDescription())
							.applicationInformation(mainService.getApplicationInformation())
							.build();
					return mainServiceWeb;
				})
				.collect(Collectors.toList());
		}
		return mainServices;
	}
	
	protected List<AdditionalServicesWeb> convertToAdditionalServicesWeb(List<EUOSBAdditionalServiceResponse> euOSBAdditionalServices) {
		if(euOSBAdditionalServices == null)
			return null;
		
		List<AdditionalServicesWeb> additionalServices = null;
		additionalServices = euOSBAdditionalServices
			.stream()
			.map(additionalService -> {
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
			})
			.collect(Collectors.toList());
		
		return additionalServices;
	}
	
	protected List<OldServicesWeb> convertToOldServicesWeb(List<EUOSBOldServiceResponse> euOldServices) {
		if(euOldServices == null)
			return null;
		
		List<OldServicesWeb> oldServices = null;
		oldServices = euOldServices
			.stream()
			.map(oldService -> {
				OldServicesWeb oldServiceWeb = OldServicesWeb.builder()
							.serviceId(oldService.getServiceId())
							.priceAfterDiscount(oldService.getPriceAfterDiscount())
							.discountPrice(oldService.getDiscountPrice())
							.price(oldService.getPrice())
							.subType(oldService.getSubType())
							.discountPercentage(oldService.getDiscountPercentage())
							.name(oldService.getName())
							.description(oldService.getDescription())
						.build();
				return oldServiceWeb;
			})
			.collect(Collectors.toList());
		
		return oldServices;
	}
	
	protected List<ServiceVoucher> convertToServiceVouchers(List<EUOSBServiceVoucher> euServiceVouchers) {
		if(euServiceVouchers == null)
			return null;
		
		List<ServiceVoucher> serviceVouchers = null;
		serviceVouchers = euServiceVouchers
			.stream()
			.map(euServiceVoucher -> {
				ServiceVoucher serviceVoucher = ServiceVoucher.builder()
							.description(euServiceVoucher.getVoucherCodeDescription())
							.amount(euServiceVoucher.getVoucherAmount())
							.percentage(euServiceVoucher.getVoucherPercentage())
							.code(euServiceVoucher.getVoucherCode())
						.build();
				return serviceVoucher;
			})
			.collect(Collectors.toList());
		
		return serviceVouchers;
	}
}
