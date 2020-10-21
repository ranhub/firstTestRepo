package com.ford.turbo.aposb.common.authsupport.environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.authsupport.environment.model.UserProvidedService;
import com.ford.turbo.aposb.common.authsupport.environment.model.VCAPServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CredentialsSource {

    private static final String CERT_AND_KEY_JSONKEY = "certAndKey";
    private static final String KEYSTORE_PASSWORD_JSONKEY = "keystorePassword";
    private static final String BASE_URI = "baseUri";
    private static final String DISABLED = "disabled";

    private Map<String, Object> properties;
    private String namespace;
    
    // Spring Cloud Config
	static ConfigPropertyService configPropertyService;
	int configPropertyServiceState = -1;

    public CredentialsSource(String name) throws IOException {
    	this.namespace = name;
        initProperties();
    }

	private void initProperties() throws IOException {
		if (isConfigCloudServerEnabled()) {
			getProperties(); //prime & check for errors on-startup
		} else {
			// obtain from environment variable(s)
			this.properties = parseUserProvidedService(this.namespace).getCredentials();
		}
	}
	
	public Map<String, Object> getProperties() {
		if (isConfigCloudServerEnabled() && configPropertyServiceState != configPropertyService.getState()) {
			configPropertyServiceState = configPropertyService.getState();
			this.properties = PropertyUtils.getProperties(configPropertyService, namespace);
		}
		return this.properties;
	}
    
    public String getBaseUri() {
        return (String) getProperties().get(BASE_URI);
    }
    
    public String getNamespace() {
    	return namespace;
    }

    public char[] getKeystorePassword() {
        String password = (String) getProperties().get(KEYSTORE_PASSWORD_JSONKEY);
        return password.toCharArray();
    }

    public InputStream getJavaKeyStore() throws URISyntaxException {
        String encoded = (String) getProperties().get(CERT_AND_KEY_JSONKEY);

        byte[] valueDecoded = Base64.decodeBase64(encoded);
        return new ByteArrayInputStream(valueDecoded);
    }
    
    public boolean isDisabled() {
    	Object disabled = getProperties().get(DISABLED);
    	if (disabled == null) return false;
    	if (disabled instanceof Boolean) return (Boolean)disabled;
    	String value = String.valueOf(disabled);
    	return value != null ? value.trim().equalsIgnoreCase("true") : false;
    }
    
    public Map<String, Object> getExtraCredentials() {
        return getProperties();
    }

    private UserProvidedService parseUserProvidedService(String name) throws IOException {
        try {
            String vcapServicesValue = getVCAPServicesEnvValue();
            VCAPServices vcapServices = new ObjectMapper().readValue(vcapServicesValue, VCAPServices.class);
            return vcapServices.getUserProvidedService(name);
        } catch (Exception e) {
            log.error("Failed to parse user-provided service \"" + name + "\"", e);
            throw e;
        }
    }

    protected String getVCAPServicesEnvValue() {
    	return new VCAPServicesEnvironment().getVCAPServicesValue();
    }
    
    public static void setConfigPropertyService(ConfigPropertyService ps) {
    	if (ps == null) throw new IllegalArgumentException("ConfigPropertyService cannot be null");
    	configPropertyService = ps;
    }
    
    public static ConfigPropertyService getConfigPropertyService() {
    	return configPropertyService;
    }
    
    static boolean isConfigCloudServerEnabled() {
    	return configPropertyService != null;
    }
}
