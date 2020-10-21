package servicebooking;

import com.ford.turbo.aposb.common.authsupport.environment.MSLApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication(scanBasePackages = {"com.ford.turbo"})
@ServletComponentScan(basePackages = {"com.ford.turbo"})
@EnableResourceServer
@EnableCircuitBreaker
public class ServiceBookingApplication {

	public static void main(String[] args) {
		 MSLApplicationRunner.start(ServiceBookingApplication.class, args);
	}
}
