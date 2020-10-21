package com.ford.turbo.aposb.common.basemodels.sleuth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomLoggingHeaderExtractionFilterTest {
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private FilterChain filterChain;
	
	@Test
	public void shouldExtractHeader_andAddToMDC() throws IOException, ServletException {
		String header = "header-value";
		CustomLoggingHeaderExtractionFilter filter = new CustomLoggingHeaderExtractionFilter();
		CustomLoggingHeaderExtractionFilter filterSpy = Mockito.spy(filter);
		
		Mockito.when(request.getHeader("x-vcap-request-id")).thenReturn(header);
		Mockito.doNothing().when(filterSpy).addMDCEntry("http.x-vcap-request-id", header);
		
		filterSpy.doFilter(request, response, filterChain);
		Mockito.verify(filterSpy).addMDCEntry("http.x-vcap-request-id", header);
	}
}
