package com.ford.turbo.aposb.common.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpTraceInterceptor.class, LoggerFactory.class })
public class HttpTraceInterceptorTest {
	private static Logger LOG;

	private HttpTraceInterceptor interceptor;
	private List<String> loggableHeaders;
	@Mock
	private HttpRequest request;
	@Mock
	private ClientHttpRequestExecution execution;
	@Mock
	private ClientHttpResponse response;

	@BeforeClass
	public static void setupClass() {
		mockStatic(LoggerFactory.class);
		when(getLogger(any(Class.class))).thenReturn(LOG = mock(Logger.class));
	}

	@Before
	public void setup() throws IOException {
		doReturn(URI.create("/example/uri")).when(this.request).getURI();
		doReturn(HttpMethod.GET).when(this.request).getMethod();
		doReturn(this.response).when(this.execution).execute(eq(this.request), any());
		doReturn(new HttpHeaders()).when(this.request).getHeaders();
		doReturn(new HttpHeaders()).when(this.response).getHeaders();
	}

	@After
	public void cleanup() {
		reset(LOG);
	}

	@Test
	public void logsRequestMethodAndPath() throws IOException {
		init(new ArrayList<>()).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[], responseHeaders=[status=0], timeTaken=",
				"ms)");
	}

	@Test
	public void logsAllWhitelistedRequestHeaders() throws IOException {
		doReturn(requestHeaders()).when(this.request).getHeaders();
		init(new ArrayList<>()).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[header1=[value1], header2=[value2]], responseHeaders=[status=0], timeTaken=",
				"ms)");
	}

	@Test
	public void logsMaskedRequestHeaders() throws IOException {
		doReturn(requestHeaders()).when(this.request).getHeaders();
		init(Arrays.asList("header1")).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[header1=**********, header2=[value2]], responseHeaders=[status=0], timeTaken=",
				"ms)");
	}

	@Test
	public void logsAllWhitelistedResponseHeaders() throws IOException {
		doReturn(responseHeaders()).when(this.response).getHeaders();
		init(new ArrayList<>()).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[], responseHeaders=[res-header2=[res-value2], res-header3=[res-value3], status=0], timeTaken=",
				"ms)");
	}

	@Test
	public void logsMaskedResponseHeaders() throws IOException {
		doReturn(responseHeaders()).when(this.response).getHeaders();
		init(Arrays.asList("res-header3")).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[], responseHeaders=[res-header2=[res-value2], res-header3=**********, status=0], timeTaken=",
				"ms)");
	}

	@Test
	public void logsResponseStatus() throws IOException {
		doReturn(200).when(this.response).getRawStatusCode();
		init(new ArrayList<>()).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[], responseHeaders=[status=200], timeTaken=",
				"ms)");
	}

	@Test
	public void logsAllWhiteListedRequestAndResponseHeaders() throws IOException {
		doReturn(requestHeaders()).when(this.request).getHeaders();
		doReturn(responseHeaders()).when(this.response).getHeaders();
		init(new ArrayList<>()).execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[header1=[value1], header2=[value2]], responseHeaders=[res-header2=[res-value2], res-header3=[res-value3], status=0], timeTaken=",
				"ms)");
	}

	@Test(expected = IOException.class)
	public void logsRequestHeadersOnIOException() throws IOException {
		doThrow(new IOException()).when(this.execution).execute(Matchers.eq(this.request), any());
		doReturn(requestHeaders()).when(this.request).getHeaders();
		doReturn(responseHeaders()).when(this.response).getHeaders();
		init(new ArrayList<>());
		try {
			execute();
		} finally {
			doAssert(
					"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[header1=[value1], header2=[value2]], responseHeaders=[], timeTaken=",
					"ms)");
		}
	}

	@Test
	public void logsHardDefaultedRequestAndResponseHeaders() throws Exception {
		doReturn(hardDefaultRequestHeaders()).when(this.request).getHeaders();
		doReturn(hardDefaultResponseHeaders()).when(this.response).getHeaders();
		init(new ArrayList<>());
		this.interceptor.afterPropertiesSet();
		execute().doAssert(
				"HttpTrace(method=GET, uri=/example/uri, requestHeaders=[Cookie=**********, Application-Id=[hard-app-id]], responseHeaders=[Set-Cookie=**********, status=0], timeTaken=",
				"ms)");
	}

	private HttpHeaders requestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header1", "value1");
		headers.add("header2", "value2");
		headers.add("header3", "value3");
		return headers;
	}

	private HttpHeaders responseHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("res-header1", "res-value1");
		headers.add("res-header2", "res-value2");
		headers.add("res-header3", "res-value3");
		return headers;
	}

	private HttpHeaders hardDefaultRequestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Application-Id", "hard-app-id");
		headers.add("Cookie", "hard-cookie");
		return headers;
	}

	private HttpHeaders hardDefaultResponseHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Set-Cookie", "hard-set-cookie");
		return headers;
	}

	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private HttpTraceInterceptorTest init(List<String> maskableHeaders) {
		this.loggableHeaders = new ArrayList() {
			{
				add("header1");
				add("header2");
				add("res-header2");
				add("res-header3");
			}
		};
		this.interceptor = new HttpTraceInterceptor(loggableHeaders, maskableHeaders);
		return this;
	}

	private HttpTraceInterceptorTest execute() throws IOException {
		this.interceptor.intercept(this.request, new byte[0], this.execution);
		return this;
	}

	private void doAssert(String start, String end) {
		ArgumentCaptor<String> message = forClass(String.class);
		verify(LOG).info(message.capture());
		assertThat(message.getValue()).startsWith(start).endsWith(end);
	}
}
