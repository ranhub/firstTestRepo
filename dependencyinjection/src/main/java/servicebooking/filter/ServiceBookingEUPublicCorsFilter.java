package servicebooking.filter;

import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns="/api/servicebooking/v1/public/*")
public class ServiceBookingEUPublicCorsFilter implements Filter {
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,	
				"auth-token,application-id,Application-Id,Content-Type");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
		
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}