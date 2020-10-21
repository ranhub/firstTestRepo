package com.ford.turbo.aposb.common.authsupport.environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.mock.env.MockEnvironment;

public class ConfigPropertyServiceTest {
	
	ConfigPropertyService configPropertyService;
	ConfigurableApplicationContext applicationContext;
	static Random random = new Random();
	
	@Before
	public void setup() {
		MockEnvironment env = new MockEnvironment();
		env.getPropertySources().addLast(createPropertySource(new HashMap<String, String>() {{
			put("PTS.baseUri", "https://ford.com/PTS");
			put("PTS.keystorePassword", "password-pts");
			put("FOO", "BAR-HIGHER-PRECEDENCE-VALUE");
		}}));
		env.getPropertySources().addLast(createPropertySource(new HashMap<String, String>() {{ //lower-precedence
			put("GCAMP.baseUri", "https://ford.com/GCAMP");
			put("GCAMP.keystorePassword", "password-gcamp");
			put("FOO", "BAR-LOWER-PRECEDENCE-VALUE");
		}}));
		
		applicationContext = new GenericApplicationContext();
		applicationContext.setEnvironment(env);
		applicationContext.refresh();
		
		configPropertyService = new ConfigPropertyService(applicationContext);
	}
		
	@Test
	public void should_returnAllPropertyNames() throws Exception {
		Collection<String> actualPropertyNames = configPropertyService.getPropertyNames();
		Collection<String> expectedPropertyNames = Arrays.asList("PTS.baseUri", "PTS.keystorePassword", "FOO", "GCAMP.baseUri", "GCAMP.keystorePassword");
		assertThat(actualPropertyNames).hasSameElementsAs(expectedPropertyNames);
	}
	
	@Test
	public void should_refreshPropertiesAndIncrementState_whenEnvironmentChangeEventOccurs() throws Exception {
		Collection<String> actualPropertyNames, expectedPropertyNames;
		
		actualPropertyNames = configPropertyService.getPropertyNames();
		expectedPropertyNames = Arrays.asList("PTS.baseUri", "PTS.keystorePassword", "FOO", "GCAMP.baseUri", "GCAMP.keystorePassword");
		assertThat(actualPropertyNames).hasSameElementsAs(expectedPropertyNames);
		int previousStateValue = configPropertyService.getState();
		
		applicationContext.getEnvironment().getPropertySources().addLast(createPropertySource(new HashMap<String, String>() {{
			put("NEW-VARIABLE", "---");
		}}));
		applicationContext.publishEvent(new EnvironmentChangeEvent(new HashSet<String>()));
		
		actualPropertyNames = configPropertyService.getPropertyNames();
		expectedPropertyNames = Arrays.asList("PTS.baseUri", "PTS.keystorePassword", "FOO", "GCAMP.baseUri", "GCAMP.keystorePassword", "NEW-VARIABLE");
		assertThat(actualPropertyNames).hasSameElementsAs(expectedPropertyNames);
		assertThat(configPropertyService.getState()).isEqualTo(previousStateValue+1);
	}

	@Test
	public void should_returnCorrectPropertyValue() throws Exception {
		assertThat(configPropertyService.getProperty("PTS.baseUri")).isEqualTo("https://ford.com/PTS");
		assertThat(configPropertyService.getProperty("PTS.baseUri", "DEFAULT-VALUE")).isEqualTo("https://ford.com/PTS");
		
		assertThat(configPropertyService.getProperty("DOES-NOT-EXIST")).isNull();
		assertThat(configPropertyService.getProperty("DOES-NOT-EXIST", "DEFAULT-VALUE")).isEqualTo("DEFAULT-VALUE");
		
		assertThat(configPropertyService.getProperty("FOO")).isEqualTo("BAR-HIGHER-PRECEDENCE-VALUE");
	}
	
	static CompositePropertySource createPropertySource(Map<String, String> map) {
		CompositePropertySource composite = new CompositePropertySource("--" + random.nextInt());
		composite.addPropertySource(new EnumerablePropertySource<String>("--" + random.nextInt()) {
			public String[] getPropertyNames() {return map.keySet().toArray(new String[0]);}
			public Object getProperty(String name) {return map.get(name);}
		});
		return composite;
	}
	
}
