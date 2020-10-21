package servicebooking;

import com.ford.cloudnative.activedirectory.oauth2.resource.config.EnableActiveDirectoryOAuth2ResourceServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Configuration
@EnableWebSecurity
@EnableActiveDirectoryOAuth2ResourceServer
public class WebSecurityConfig {

    @Value("${com.ford.msl.ip-whitelist}")
    private String[] ipWhiteList;

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurer(SecurityProperties securityProperties) {
        return new WebSecurityConfigurerAdapter() {
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http.csrf()
                	.disable()
	            .authorizeRequests()
	            .antMatchers("/api/servicebooking/v1/convoy.html").authenticated()
	            	.requestMatchers(request -> {
			           	 if(request.getRequestURI().contains("/api/servicebooking/v1") || 
			           			request.getRequestURI().contains("/api/servicebooking/v2")) {
			           	 	return true;
			           	 }
			           	 return false;
			        	}).permitAll()
	                .anyRequest().access("isAuthenticated() and (" + getIpWhitelistSpel() + ")")
	
	            .and()
	                .httpBasic();
			}
		};
    }
    
    @Bean
    public ResourceServerConfigurer resourceServerConfigurer() {
    	return new ResourceServerConfigurerAdapter() {
    		@Override
    		public void configure(HttpSecurity http) throws Exception {
    			http
    			.csrf()
    				.disable()
    			.requestMatcher(request -> {
    				return (request.getRequestURI() != null && 
    							request.getRequestURI().contains("/v2/apptoapp"));
    			})
    			.authorizeRequests()
    				.anyRequest()
    					.access("#oauth2.isClient() and #oauth2.hasScope('msl.servicebooking')");
    		}
    	};
    }

    private String getIpWhitelistSpel() {
        return stream(ipWhiteList)
                .map(ip -> String.format("hasIpAddress('%s')", ip))
                .collect(joining(" or "));
    }
}
