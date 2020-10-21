package com.ford.turbo.aposb.common.authsupport.environment;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import io.pivotal.spring.cloud.service.common.ConfigServerServiceInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudConfigProfileInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<ApplicationPreparedEvent>, Ordered {
	
	ConfigPropertyService propertyService;
	
	@Override
	public void initialize(final ConfigurableApplicationContext applicationContext) {
		// register ConfigPropertyService bean
		propertyService = new ConfigPropertyService(applicationContext);
		applicationContext.getBeanFactory().registerSingleton("propertyService", propertyService);
		
		if (isCloudConfigServerServiceBound() || useLocalCloudConfig()) {
			log.info("Using Config Property Service for (credentials) configuration");
			CredentialsSource.setConfigPropertyService(propertyService);
		} else {
			log.info("Using VCAP for (credentials) configuration");
		}
	}

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		propertyService.refresh();
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private boolean useLocalCloudConfig() {
		return System.getProperties().getProperty("LOCAL_CLOUD_CONFIG", "0").equals("1");
	}

	boolean isCloudConfigServerServiceBound() {
        try {
        	new CloudFactory().getCloud().getServiceInfos().stream().filter(p -> p instanceof ConfigServerServiceInfo).findFirst().get();
        	return true;
        } catch (Exception e) {
            // not running a cloud environment OR service info not found
        	return false;
        }
	}
}