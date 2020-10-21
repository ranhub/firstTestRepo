package servicebooking;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceBookingCorsFilter implements Filter {
	@Autowired
	@Qualifier("CORS_ALLOWED_ORIGINS")
	private CredentialsSource corsAllowedOrigins;

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		String origin = request.getHeader(HttpHeaders.ORIGIN);
		List<String> corsAllowedOrigins = (List<String>) this.corsAllowedOrigins.getProperties().get("origins");
		if (corsAllowedOrigins.contains(origin)) {
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
					"auth-token,application-id,Application-Id,Content-Type");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

			if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}