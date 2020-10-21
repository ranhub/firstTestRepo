package com.ford.turbo.aposb.common.interceptors;

import static java.util.Arrays.asList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLoggingInterceptor  implements HttpRequestInterceptor, InitializingBean {
	
	private static final String MASK = "**********";
	private final List<String> loggableHeaders;
	private final List<String> maskableHeaders;
	
	public RequestLoggingInterceptor(
			@Value("#{'${http.client.interceptor.header.loggable:}'.split(',')}") List<String> loggableHeaders,
			@Value("#{'${http.client.interceptor.header.loggable.mask:}'.split(',')}") List<String> maskableHeaders) {
		this.loggableHeaders = loggableHeaders;
		this.maskableHeaders = maskableHeaders;
	}
	
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        StringBuilder sb = new StringBuilder(1000);
        if (request instanceof HttpUriRequest) {
            HttpUriRequest uriRequest = (HttpUriRequest) request;
            sb.append(uriRequest.getMethod())
                    .append(" ")
                    .append(uriRequest.getURI())
                    .append(" ");
        }
        this.loggableHeaders.forEach(header -> {
			Object value = request.getFirstHeader(header);
			if (value != null) {
				sb.append(" "+(this.maskableHeaders.contains(header) ? MASK : value.toString()));
			}
		});
        log.info(sb.toString());

        if (log.isDebugEnabled() && request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest requestWithBody = (HttpEntityEnclosingRequest) request;
            ByteArrayOutputStream buf = new ByteArrayOutputStream(5000);
            requestWithBody.getEntity().writeTo(buf);
            log.debug(buf.toString());
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
		this.loggableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie", "Application-Id",
				"X-B3-Spanid", "X-B3-Traceid"));
		this.maskableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie"));
	}
}