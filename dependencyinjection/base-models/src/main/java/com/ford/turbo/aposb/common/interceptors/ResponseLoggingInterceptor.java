package com.ford.turbo.aposb.common.interceptors;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResponseLoggingInterceptor  implements HttpResponseInterceptor, InitializingBean {

	private static final String MASK = "**********";
	private final List<String> loggableHeaders;
	private final List<String> maskableHeaders;
	
	public ResponseLoggingInterceptor(
			@Value("#{'${http.client.interceptor.header.loggable:}'.split(',')}") List<String> loggableHeaders,
			@Value("#{'${http.client.interceptor.header.loggable.mask:}'.split(',')}") List<String> maskableHeaders) {
		this.loggableHeaders = loggableHeaders;
		this.maskableHeaders = maskableHeaders;
	}
	
    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(response.getStatusLine().getStatusCode());
        
        this.loggableHeaders.forEach(header -> {
			Object value = response.getFirstHeader(header);
			if (value != null) {
				sb.append(" "+ (this.maskableHeaders.contains(header) ? MASK : value.toString()));
			}
		});
        log.info(sb.toString());
    }
    
    @Override
   	public void afterPropertiesSet() throws Exception {
   		this.loggableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie", "Application-Id",
   				"X-B3-Spanid", "X-B3-Traceid"));
   		this.maskableHeaders.addAll(asList("auth-token", "Authorization", "Cookie", "Set-Cookie"));
   	}
}