package com.ford.turbo.aposb.common.basemodels.sleuth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.mock.web.MockHttpServletResponse;

public class SleuthFilterTest {

	Tracer tracer;
	
	@Before
	public void setup() {
        tracer = mock(Tracer.class);
        Span span = Span.builder().traceId(10).spanId(20).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(span);
	}
	
    @Test
    public void should_addSleuthHeadersToResponse() throws ServletException, IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		new SleuthFilter(tracer).doFilterInternal(mock(HttpServletRequest.class), response, mock(FilterChain.class));
		
		assertEquals(tracer.getCurrentSpan().traceIdString(), response.getHeader(Span.TRACE_ID_NAME));
		assertEquals(Span.idToHex(tracer.getCurrentSpan().getSpanId()), response.getHeader(Span.SPAN_ID_NAME));
    }
    
    @Test
    public void should_notOverwriteExistingSleuthHeadersInResponse() throws ServletException, IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.addHeader(Span.TRACE_ID_NAME, "TRACE-ID");
		response.addHeader(Span.SPAN_ID_NAME, "SPAN-ID");
		new SleuthFilter(tracer).doFilterInternal(mock(HttpServletRequest.class), response, mock(FilterChain.class));
		
		assertEquals("TRACE-ID", response.getHeader(Span.TRACE_ID_NAME));
		assertEquals("SPAN-ID", response.getHeader(Span.SPAN_ID_NAME));
    }

    @Test
    public void should_notWriteSleuthHeaders_whenCurrentSpanIsNull() throws ServletException, IOException {
    	tracer = mock(Tracer.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		new SleuthFilter(tracer).doFilterInternal(mock(HttpServletRequest.class), response, mock(FilterChain.class));
		
		assertFalse(response.getHeaderNames().contains(Span.TRACE_ID_NAME));
		assertFalse(response.getHeaderNames().contains(Span.SPAN_ID_NAME));
    }
    
}
