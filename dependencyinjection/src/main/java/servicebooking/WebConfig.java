package servicebooking;

import com.ford.turbo.aposb.common.authsupport.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Profile("!test")
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	private final AuthorizationInterceptor authorizationInterceptor;

	@Autowired
	public WebConfig(AuthorizationInterceptor authorizationInterceptor) {
		this.authorizationInterceptor = authorizationInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.authorizationInterceptor).addPathPatterns("/**/v1/public/**");
	}
}