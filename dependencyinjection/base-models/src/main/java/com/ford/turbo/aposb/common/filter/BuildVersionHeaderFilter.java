package com.ford.turbo.aposb.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class BuildVersionHeaderFilter extends OncePerRequestFilter {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * Each application sets its version during the Gradle build. Here we default to DEV so we have something to find
     * during integration tests that run beforehand.
     */
    @Value("${spring.application.version:DEV}")
    private String buildVersion;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-Build-Version", appName + " " + buildVersion);
        filterChain.doFilter(request, response);
    }
}
