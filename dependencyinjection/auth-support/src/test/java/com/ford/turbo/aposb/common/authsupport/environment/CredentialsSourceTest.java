package com.ford.turbo.aposb.common.authsupport.environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.ford.turbo.aposb.common.authsupport.CredentialsSourceHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class CredentialsSourceTest {
	
	private static final String TEST_NAMESPACE="PTS";
	private static Map<String, Object> testProperties;
	
	private static ConfigPropertyService configPropertyService;
	private static int currentState = 0;
	
	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		final Properties properties = new Properties();
		properties.load(new ClassPathResource("test-values.properties").getInputStream());
		
		configPropertyService = new ConfigPropertyService(new GenericApplicationContext()) {
			@Override
			public Collection<String> getPropertyNamesStartingWith(String prefix) {
				return properties.stringPropertyNames().stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
			}
			
			@Override
			public String getProperty(String name) {
				return properties.getProperty(name);
			}
			
			@Override
			public int getState() {
				return currentState;
			}
		};
		
		testProperties = new HashMap<String, Object>() {{
			put("baseUri", "https://ford.com/PTS");
			put("certAndKey", "Y2VydC1rZXk=");  //base64 encoded for 'cert-key' value
			put("keystorePassword", "password");
		}};
	}
	
	@After
	public void runAfter() {
		CredentialsSource.configPropertyService = null; //reset
	}
	
	@Test
	public void should_returnPropertiesMatchingTestValues_withPropertyConfigService() throws Exception {
		CredentialsSource.setConfigPropertyService(configPropertyService);
		CredentialsSource credentialsSource = new CredentialsSource(TEST_NAMESPACE);
		assertEquals(testProperties, credentialsSource.getProperties());
	}
	
	@Test
	public void should_haveCorrectCredentialValues_withPropertyConfigService() throws Exception {
		CredentialsSource.setConfigPropertyService(configPropertyService);
		CredentialsSource credentialsSource = new CredentialsSource(TEST_NAMESPACE);
		assertEquals(testProperties, credentialsSource.getExtraCredentials());
		assertEquals(testProperties.get("baseUri"), credentialsSource.getBaseUri());
		assertEquals(convertStreamToString(new ByteArrayInputStream(Base64.decodeBase64((String)testProperties.get("certAndKey")))), convertStreamToString(credentialsSource.getJavaKeyStore()));
		assertEquals(new String(((String)testProperties.get("keystorePassword")).toCharArray()), new String(credentialsSource.getKeystorePassword()));
	}
	
	@Test
	public void should_returnSamePropertiesEachTime_withPropertyConfigService() throws Exception {
		CredentialsSource.setConfigPropertyService(configPropertyService);
		CredentialsSource credentialsSource = new CredentialsSource(TEST_NAMESPACE);
		assertSame(credentialsSource.getProperties(), credentialsSource.getProperties());
		assertSame(credentialsSource.getProperties(), credentialsSource.getExtraCredentials());
	}
	
	@Test
	public void should_returnDifferentInstanceOfProperties_withPropertyConfigServiceStateUpdate() throws Exception {
		CredentialsSource.setConfigPropertyService(configPropertyService);
		CredentialsSource credentialsSource = new CredentialsSource(TEST_NAMESPACE);
		Map<String, Object> properties1 = credentialsSource.getProperties();
		currentState++;
		Map<String, Object> properties2 = credentialsSource.getProperties();
		Map<String, Object> properties3 = credentialsSource.getProperties();
		assertEquals(properties1, properties2);
		assertEquals(properties2, properties3);
		assertNotSame(properties1, properties2);
		assertSame(properties2, properties3);
	}

    @Test
    public void should_decodeKeystoreFromVcapServices() throws IOException, URISyntaxException {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");

        InputStream inputStream = credentialsSource.getJavaKeyStore();
        String javaKeyStore = convertStreamToString(inputStream);

        assertThat(javaKeyStore).isEqualTo("This is a test of using a certificate from a user provided service in cf");
    }

    @Test
    public void should_retrievePasswordFromVcapServices() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
        assertThat(credentialsSource.getKeystorePassword()).isEqualTo("xyz".toCharArray());
    }
    
    @Test
    public void should_returnNotDisabledByDefault_when_disabledFlagIsMissing() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
        assertThat(credentialsSource.isDisabled()).isFalse();
    }
    
    @Test
    public void should_returnDisabledByDefault_when_disabledFlagIsPresentAndValueIsTrue() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValuesDisabled("PingCredsDisabled");
        assertThat(credentialsSource.isDisabled()).isTrue();
    }
    
    @Test
    public void should_returnNotDisabledByDefault_when_disabledFlagIsPresentAndValueIsNotTrue() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValuesNotDisabled("PingCredsNotDisabled");
        assertThat(credentialsSource.isDisabled()).isFalse();
    }

    @Test
    public void should_getUrlFromVcapServices() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.getCredentialsSourceWithSimpleValues("PingCreds");
        assertThat(credentialsSource.getBaseUri()).isEqualTo("https://testbaseurl");
    }

    @Test
    public void should_loadFineWithOtherVCAPServices() throws Exception {
        CredentialsSource credentialsSource = CredentialsSourceHelper.givenCredentialsWithOtherServices();
        assertThat(credentialsSource.getBaseUri()).isEqualTo("https://testbaseurl");
    }
    
    @Test(expected = IllegalArgumentException.class)
	public void should_throwException_whenSettingNullConfigPropertyService() throws Exception {
		CredentialsSource.setConfigPropertyService(null);
	}
    
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}