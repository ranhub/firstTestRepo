package com.ford.turbo.aposb.common.authsupport.environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ford.turbo.aposb.common.basemodels.hystrix.BaseCommandExecutionHook;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.HystrixPlugins;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MSLApplicationRunner {
	
	public static void start(Object source, String... args) {
		HystrixPlugins.getInstance().registerCommandExecutionHook(new BaseCommandExecutionHook());

		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(source);
		addSpaceProfiles(springApplicationBuilder);
		addCloudConfig(springApplicationBuilder);
		springApplicationBuilder.run(args);

		//Watchdog.startWatchdog();
	}

	public static SpringApplicationBuilder addCloudConfig(SpringApplicationBuilder builder) {
		CloudConfigProfileInitializer cloudConfigProfileInitializer = new CloudConfigProfileInitializer();
		return builder
				.initializers(cloudConfigProfileInitializer)
				.listeners(cloudConfigProfileInitializer);
	}
	
	public static SpringApplicationBuilder addSpaceProfiles(SpringApplicationBuilder builder) {
		List<String> spaceProfiles = getAdditionalProfilesForSpaceEnvironment(getSpaceName());
		return builder.profiles(spaceProfiles.toArray(new String[0]));
	}
	
	protected static String getSpaceName() {
		String overrideSpaceName = System.getenv("LOCAL_SPACE_NAME");
		if (StringUtils.isNotBlank(overrideSpaceName)) {
			log.info("Detected (overriden) space name: {}", overrideSpaceName);
			return overrideSpaceName;
		}
		
		String vcapApplicationJson = System.getenv("VCAP_APPLICATION");
		if (StringUtils.isNotBlank(vcapApplicationJson)) {
			try {
				String spaceName = new ObjectMapper().readTree(vcapApplicationJson).get("space_name").textValue();
				log.info("Detected space name: {}", spaceName);
				return spaceName;
			} catch (Exception e) {
				//slient fail
			}
		}
		
		log.info("No space name was detected");
		return null;
	}
	
	protected static List<String> getAdditionalProfilesForSpaceEnvironment(String spaceName) {
		if (StringUtils.isBlank(spaceName)) {
			return Collections.emptyList();
		}
		
		//e.g. Prod_US_East_FordSvcs, MSL_US_EAST_STAGE_FORDSvcs
		String spaceNameTokens[] = spaceName.toLowerCase().split("_");
		if (spaceNameTokens.length < 4) {
			return Collections.emptyList();
		}
		
		String region = spaceNameTokens[1];
		String subRegion = spaceNameTokens[2];
		String environment = spaceNameTokens[0].equals("prod") ? spaceNameTokens[0] : spaceNameTokens[3];
		
		//e.g. all, us, us-east, dev, dev-us, dev-us-east	
		return Arrays.asList(
				"all",
				region, 
				region + "-" + subRegion,
				environment,
				environment + "-" + region, 
				environment+ "-" + region + "-" + subRegion
				);
	}

}
