package com.ford.turbo.aposb.common.basemodels.sleuth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SleuthFilter extends OncePerRequestFilter {

	private Tracer tracer;

	@Autowired
	public SleuthFilter(Tracer tracer) {
		this.tracer = tracer;
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (!response.containsHeader(Span.SPAN_ID_NAME)) {
			Span currentSpan = this.tracer.getCurrentSpan();
			if (currentSpan != null) {			
				response.addHeader(Span.TRACE_ID_NAME, currentSpan.traceIdString());
				response.addHeader(Span.SPAN_ID_NAME, Span.idToHex(currentSpan.getSpanId()));
			}
		}
        filterChain.doFilter(request, response);
    }
}
