package com.ford.turbo.aposb.common.authsupport.validator;

import java.io.IOException;
import java.util.Collections;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.CustomLoggingHeaderExtractionFilter;
import com.ford.turbo.aposb.common.basemodels.sleuth.SleuthFilter;
import com.ford.turbo.aposb.common.filter.BuildVersionHeaderFilter;
import com.ford.turbo.aposb.common.filter.TimingFilter;
import com.ford.turbo.aposb.common.interceptors.HttpTraceInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthTokenValidatorConfiguration {
    @Bean
    @Qualifier("FIG_AUTHENTICATION")
    public CredentialsSource figServiceInfo() throws IOException {
        return new CredentialsSource("FIG_AUTHENTICATION");
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate restTemplate(HttpTraceInterceptor httpTraceInterceptor) {
    	HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    	httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(10000); //time to get the connection from pool
    	httpComponentsClientHttpRequestFactory.setConnectTimeout(15000); // time to establish the connection
    	httpComponentsClientHttpRequestFactory.setReadTimeout(32000); //time to read the data once connection is established
    	RestTemplate rt = new RestTemplate(httpComponentsClientHttpRequestFactory);
    	rt.setInterceptors(Collections.singletonList(httpTraceInterceptor));
        return rt;
    }
    
    @Bean
    @Qualifier("customLoggingHeaderExtractionFilterBean")
    public FilterRegistrationBean customLoggingHeaderExtractionFilterBean(CustomLoggingHeaderExtractionFilter customLoggingHeaderExtractionFilter) {
    	FilterRegistrationBean registration = new FilterRegistrationBean(customLoggingHeaderExtractionFilter);
    	registration.setOrder(Integer.MAX_VALUE - 4);
    	registration.setName("headerExtractionFilter");
    	return registration;
    }

    @Bean
    @Qualifier("sleuthFilterBean")
    public FilterRegistrationBean sleuthFilterBean(SleuthFilter sleuthFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(sleuthFilter);
        registration.setOrder(Integer.MAX_VALUE - 3);
        registration.setName("sleuthFilter");
        return registration;
    }
    
    
    @Bean
    @Qualifier("buildVersionHeaderFilterBean")
    public FilterRegistrationBean buildVersionHeaderFilterBean(BuildVersionHeaderFilter headerFitler) {
        FilterRegistrationBean registration = new FilterRegistrationBean(headerFitler);
        registration.setOrder(Integer.MAX_VALUE - 2);
        registration.setName("versionHeaderFilter");
        return registration;
    }
    
    @Bean
    @Qualifier("timingFilterBean")
    public FilterRegistrationBean timingFilterBean(TimingFilter timingFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(timingFilter);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.setName("timingFilter");
        return registration;
    }
}
