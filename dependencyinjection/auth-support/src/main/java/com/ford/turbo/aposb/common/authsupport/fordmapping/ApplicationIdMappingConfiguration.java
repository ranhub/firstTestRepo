package com.ford.turbo.aposb.common.authsupport.fordmapping;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ApplicationIdMappingConfiguration {

    @Bean
    @Qualifier("APPLICATION_ID_MAPPINGS")
    public CredentialsSource continentInfo() throws IOException {
        return new CredentialsSource("APPLICATION_ID_MAPPINGS");
    }

}
