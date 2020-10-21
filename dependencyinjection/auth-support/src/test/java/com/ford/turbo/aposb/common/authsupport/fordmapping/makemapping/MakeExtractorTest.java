package com.ford.turbo.aposb.common.authsupport.fordmapping.makemapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.models.Make;

public class MakeExtractorTest {
    private CredentialsSource continentCredentials;

    @Before
    public void setup() throws IOException {
    	final String vcap = IOUtils.toString(new ClassPathResource("VCAP_Application_ID_Mappings").getURI());
        	continentCredentials = new CredentialsSource("APPLICATION_ID_MAPPINGS") {
        	@Override
			protected String getVCAPServicesEnvValue() {
				return vcap;
			}
        };
    }

    @Test
    public void should_returnCorrectMake_whenAppIdIsValid() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake("APPID-NA-FORD-1")).isEqualTo(Make.FORD);
        assertThat(makeExtractor.getMake("APPID-NA-FORD-2")).isEqualTo(Make.FORD);
        assertThat(makeExtractor.getMake("APPID-NA-LINCOLN-1")).isEqualTo(Make.LINCOLN);
        assertThat(makeExtractor.getMake("APPID-EU-FORD-1")).isEqualTo(Make.FORD);
        assertThat(makeExtractor.getMake("APPID-AP-LINCOLN-1")).isEqualTo(Make.LINCOLN);
        assertThat(makeExtractor.getMake("APPID-SA-LINCOLN-1")).isEqualTo(Make.LINCOLN);
    }

    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_DoesNotExist() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake("APPID-DOES-NOT-EXIST")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_BlankAppId() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake("")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_nullAppId() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake(null)).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_InvalidMake() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake("APPID-NA-INVALIDMAKE-1")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_InvalidContinent() {
        MakeExtractor makeExtractor = new MakeExtractor(continentCredentials);
        assertThat(makeExtractor.getMake("APPID-INVALIDCONTINENT-FORD-1")).isNull();
    }
}