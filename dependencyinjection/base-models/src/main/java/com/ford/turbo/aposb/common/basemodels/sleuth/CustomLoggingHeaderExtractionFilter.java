package com.ford.turbo.aposb.common.basemodels.sleuth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CustomLoggingHeaderExtractionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 try {
	        	HttpServletRequest httpRequest = (HttpServletRequest) request;
	            String mdcData = httpRequest.getHeader("x-vcap-request-id");
	            addMDCEntry("http.x-vcap-request-id", mdcData);
	            filterChain.doFilter(request, response);
	        } finally {
	            MDC.clear();
	        }
	}

	protected void addMDCEntry(String key, String mdcData) {
		MDC.put(key, mdcData);
	}
}
