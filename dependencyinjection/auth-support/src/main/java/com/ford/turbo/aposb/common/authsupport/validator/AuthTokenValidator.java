package com.ford.turbo.aposb.common.authsupport.validator;

import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AppIdNotFoundException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotFoundException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthTokenValidator {

    private final ObjectFactory<ValidateAuthTokenCommand> commandFactory;
	private ContinentCodeExtractor continentCodeExtractor;

    @Autowired
    public AuthTokenValidator(ObjectFactory<ValidateAuthTokenCommand> commandFactory, ContinentCodeExtractor continentCodeExtractor) {
        this.commandFactory = commandFactory;
        this.continentCodeExtractor = continentCodeExtractor;
    }

    public UserIdentity checkValid(String authToken, String sdnAppId) {
        checkAuthToken(authToken);
        checkAppId(sdnAppId);

        log.debug("Begining FIG validation for Application-Id: " + sdnAppId);
        
        ValidateAuthTokenCommand command = commandFactory.getObject();
        command.setAuthToken(authToken);
        command.setAppId(sdnAppId);

        return command.execute();
    }

    /**
     * Throws an exception if the auth token is null or empty
     */
    public void checkAuthToken(String authToken) {
        if (!StringUtils.hasText(authToken)) {
            throw new AuthTokenNotFoundException();
        }
    }

    /**
     * Throws an exception if the app id is null or empty
     */
    public void checkAppId(String appId) {
        if (!StringUtils.hasText(appId)) {
            throw new AppIdNotFoundException();
        }
    }

	public void checkValidAppId(String appId) {
		ContinentCode continentCode = this.continentCodeExtractor.getContinent(appId); //Continent code extractor is based on Application Id Mappings
		if (continentCode == null) {
			throw new UnknownAppIdException(appId);
		}
	}
}