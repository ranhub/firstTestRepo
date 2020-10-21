package com.ford.turbo.aposb.common.authsupport.validator;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AppIdNotFoundException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@Component
@Validated
public class ApplicationIdValidator {
	private ContinentCodeExtractor continentCodeExtractor;
	
	public ApplicationIdValidator(ContinentCodeExtractor continentCodeExtractor) {
		this.continentCodeExtractor = continentCodeExtractor;
	}
	
    public void checkEmpty(String appId) {
        if (!StringUtils.hasText(appId)) {
            throw new AppIdNotFoundException();
        }
    }

	public void checkValidAppId(String appId) {
		//Continent code extractor is based on Application Id Mappings
		checkEmpty(appId);
		ContinentCode continentCode = continentCodeExtractor.getContinent(appId); 
		if (continentCode == null) {
			throw new UnknownAppIdException(appId);
		}
	}
	
	public void checkValidRegionalAppId(String appId, @NotNull List<ContinentCode> whiteListRegions) {
		checkEmpty(appId);
		ContinentCode continentCode = continentCodeExtractor.getContinent(appId); 
		if (continentCode == null) {
			throw new UnknownAppIdException(appId);
		}
		
		for(ContinentCode regionCode : whiteListRegions) {
			if(continentCode.equals(regionCode)) {
				return;
			}
		}
		throw new NoBackendAvailableException();
	}
}
