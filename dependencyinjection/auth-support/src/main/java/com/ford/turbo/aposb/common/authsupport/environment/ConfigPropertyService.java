package com.ford.turbo.aposb.common.authsupport.environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPropertyService {
	
	protected ConfigurableApplicationContext applicationContext;
	
	protected Collection<String> propertyNames;
	AtomicInteger state = new AtomicInteger();

	public ConfigPropertyService(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		applicationContext.addApplicationListener((EnvironmentChangeEvent evt) -> {refresh();});
		refresh();
	}
	
	public void refresh() {
		log.info("performing REFRESH of properties");
		this.propertyNames = Collections.unmodifiableCollection(collectAllPropertyNames());
		state.incrementAndGet();
	}
	
	public int getState() {
		return state.get();
	}
	
	public String getProperty(String name) {
		return this.applicationContext.getEnvironment().getProperty(name);
	}
	
	public String getProperty(String name, String defaultValue) {
		return this.applicationContext.getEnvironment().getProperty(name, defaultValue);
	}
	
	public Collection<String> getPropertyNames() {
		return this.propertyNames;
	}
	
	public Collection<String> getPropertyNamesStartingWith(String prefix) {
		return getPropertyNames().stream().filter(propertyName -> propertyName.startsWith(prefix)).collect(Collectors.toList());
	}
	
	// http://stackoverflow.com/questions/23506471/spring-access-all-environment-properties-as-a-map-or-properties-object
	protected Collection<String> collectAllPropertyNames() {
		Collection<String> result = new HashSet<>();
		this.applicationContext.getEnvironment().getPropertySources().forEach(ps -> result.addAll(collectAllPropertyNames(ps)));
		return result;
	}

	protected Collection<String> collectAllPropertyNames(PropertySource<?> aPropSource) {
		Collection<String> result = new HashSet<>();

		if (aPropSource instanceof CompositePropertySource) {
			CompositePropertySource cps = (CompositePropertySource) aPropSource;
			cps.getPropertySources().forEach(ps -> result.addAll(collectAllPropertyNames(ps)));
			return result;
		}

		if (aPropSource instanceof EnumerablePropertySource<?>) {
			EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
			result.addAll(Arrays.asList(ps.getPropertyNames()));
			return result;
		}

		return result;
	}
	
}
