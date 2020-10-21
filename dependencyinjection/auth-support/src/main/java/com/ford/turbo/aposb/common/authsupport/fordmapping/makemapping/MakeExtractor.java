package com.ford.turbo.aposb.common.authsupport.fordmapping.makemapping;

import com.ford.turbo.aposb.common.authsupport.fordmapping.ApplicationIdMapping;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.models.Make;

@Component
@RefreshScope
public class MakeExtractor extends ApplicationIdMapping {

    @Autowired
    public MakeExtractor(@Qualifier("APPLICATION_ID_MAPPINGS") CredentialsSource continentCredentials) {
        super(continentCredentials);
    }

    public Make getMake(String appId){

		AppIdAttributes appIdAttributes = this.attributesByAppId.get(appId);
		if (appIdAttributes != null) {
			return appIdAttributes.getMake();
		}

        throw new UnknownAppIdException(appId);
    }
}
