package com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.fordmapping.ApplicationIdMapping;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@Component
@RefreshScope
public class ContinentCodeExtractor extends ApplicationIdMapping {
	
    @Autowired
    public ContinentCodeExtractor(@Qualifier("APPLICATION_ID_MAPPINGS") CredentialsSource continentCredentials) {
        super(continentCredentials);
    }

	public ContinentCode getContinent(String appId){

		if(!StringUtils.isEmpty(appId)) {
			AppIdAttributes appIdAttributes = this.attributesByAppId.get(appId.toUpperCase());
			if (appIdAttributes != null) {
				return appIdAttributes.getContinentCode();
			}
		}

        throw new UnknownAppIdException(appId);
    }
}
