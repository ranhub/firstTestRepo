package com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

public class ContinentCodeExtractorTest {

    private CredentialsSource continentCredentials;

    @Before
    public void setup() throws IOException {
    	final String vcap = IOUtils.toString(new ClassPathResource("VCAP_Application_ID_Mappings").getURI());
        continentCredentials = new CredentialsSource("APPLICATION_ID_MAPPINGS") {
        	@Override
        	protected String getVCAPServicesEnvValue() {return vcap;}
        };
    }

    @Test
    public void should_returnCorrectContinentCode_whenAppIdIsValid() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("APPID-NA-FORD-1")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("APPID-NA-FORD-2")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("APPID-NA-LINCOLN-1")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("APPID-EU-FORD-1")).isEqualTo(ContinentCode.EU);
        assertThat(continentCodeExtractor.getContinent("APPID-AP-LINCOLN-1")).isEqualTo(ContinentCode.AP);
        assertThat(continentCodeExtractor.getContinent("APPID-SA-LINCOLN-1")).isEqualTo(ContinentCode.SA);
    }
    
    @Test
    public void should_returnCorrectContinentCode_whenAppIdIsValidAndInLowerCase() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("appid-na-FORD-1")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("appid-NA-FORD-2")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("appid-NA-LINCOLN-1")).isEqualTo(ContinentCode.NA);
        assertThat(continentCodeExtractor.getContinent("appid-eu-FORD-1")).isEqualTo(ContinentCode.EU);
        assertThat(continentCodeExtractor.getContinent("appid-ap-LINCOLN-1")).isEqualTo(ContinentCode.AP);
        assertThat(continentCodeExtractor.getContinent("appid-sa-lincoln-1")).isEqualTo(ContinentCode.SA);
    }
    
    

    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_DoesNotExist() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("APPID-DOES-NOT-EXIST")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_BlankAppId() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_nullAppId() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent(null)).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_InvalidMake() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("APPID-NA-INVALIDMAKE-1")).isNull();
    }
    
    @Test(expected = UnknownAppIdException.class)
    public void should_throwException_whenAppIdIsInvalid_InvalidContinent() {
        ContinentCodeExtractor continentCodeExtractor = new ContinentCodeExtractor(continentCredentials);
        assertThat(continentCodeExtractor.getContinent("APPID-INVALIDCONTINENT-FORD-1")).isNull();
    }
}
