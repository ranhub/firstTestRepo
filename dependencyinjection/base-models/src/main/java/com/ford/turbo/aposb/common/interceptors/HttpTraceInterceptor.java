package com.ford.turbo.aposb.common.interceptors;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;

import com.ford.turbo.aposb.common.interceptors.models.HttpTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class HttpTraceInterceptor implements ClientHttpRequestInterceptor, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(HttpTraceInterceptor.class);
	private static final String MASK = "**********";

	private final List<String> loggableHeaders;
	private final List<String> maskableHeaders;

	public HttpTraceInterceptor(
			@Value("#{'${http.client.interceptor.header.loggable:}'.split(',')}") List<String> loggableHeaders,
			@Value("#{'${http.client.interceptor.header.loggable.mask:}'.split(',')}") List<String> maskableHeaders) {
		this.loggableHeaders = loggableHeaders;
		this.maskableHeaders = maskableHeaders;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		long start = System.currentTimeMillis();
		HttpTrace trace = new HttpTrace(request.getURI().toString(), request.getMethod().toString());
		headers(trace.getRequestHeaders(), request.getHeaders());
		ClientHttpResponse response = null;
		try {
			response = execution.execute(request, body);
			headers(trace.getResponseHeaders(), response.getHeaders());
			trace.getResponseHeaders().add("status=" + response.getRawStatusCode());
		} finally {
			trace.setTimeTaken((System.currentTimeMillis() - start) + "ms");
			LOG.info(trace.toString());
		}
		return response;
	}

	private void headers(List<String> headers, HttpHeaders httpHeaders) {
		this.loggableHeaders.forEach(header -> {
			Object value = httpHeaders.get(header);
			if (value != null) {
				headers.add(header + "=" + (this.maskableHeaders.contains(header) ? MASK : value.toString()));
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.loggableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie", "Application-Id",
				"X-B3-Spanid", "X-B3-Traceid"));
		this.maskableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie"));
	}
}
