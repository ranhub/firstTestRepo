package com.ford.turbo.aposb.common.authsupport.environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class PropertyUtilsTest {
	
	private String namespace;
	private static ConfigPropertyService configPropertyService;
	
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
		};
	}
	
	@Test
	public void shouldReturnEmptyMap_whenNameSpaceIsNotProvided() {
		Map<String, Object> properties = PropertyUtils.getProperties(configPropertyService, null);

		Assert.assertEquals(0, properties.size());
	}
	
	@Test
	public void shouldReturnEmptyMap_whenNameSpaceIsNotAvailable() {
		Map<String, Object> properties = PropertyUtils.getProperties(configPropertyService, "DOES-NOT-EXIST");

		Assert.assertEquals(0, properties.size());
	}
	
	@Test
	public void testUltimateNestedNamespace() {
		namespace = "TEST_APPLICATION_ID_MAPPINGS";
		Map<String, Object> actualProperties = PropertyUtils.getProperties(configPropertyService, namespace);
		
		Map<String, Object> expectedProperties = new HashMap<String, Object>() {{
			put("NA", new HashMap<String, Object>() {{
				put("Ford", Arrays.asList("APP-NA-FORD-1","APP-NA-FORD-2","APP-NA-FORD-3","APP-NA-FORD-4"));
				put("Lincoln", Arrays.asList("APP-NA-LINCOLN-1"));
				put("Mazda", new HashMap<String, Object>() {{
					put("year", "2012");
					put("models", Arrays.asList("MODEL-1","MODEL-2"));
					put("make", Arrays.asList("MAZDA"));
				}});
			}});
			put("AP", new HashMap<String, Object>() {{
				put("Ford", Arrays.asList("APP-AP-FORD-1","APP-AP-FORD-2","APP-AP-FORD-3"));
			}});
			put("EU", new HashMap<String, Object>() {{
				put("Lincoln", Arrays.asList("APP-EU-LINCOLN-1","APP-EU-LINCOLN-2"));
			}});
			put("BR", new HashMap<String, Object>() {{
				put("Lincoln", Arrays.asList(
							new HashMap<String, Object>() {{
								put("name", "APP-BR-LINCOLN-1-name");
								put("id", "APP-BR-LINCOLN-1");
							}},
							new HashMap<String, Object>() {{
								put("name", "APP-BR-LINCOLN-2-name");
								put("id", "APP-BR-LINCOLN-2");
							}}
						));
			}});
		}};

		
		Assert.assertEquals(expectedProperties, actualProperties);
	}
	
	

	
}
