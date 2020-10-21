package com.ford.turbo.aposb.common.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TimingFilterTest {

    @Test
    public void should_logTotalExecutionTime() throws ServletException, IOException {
        ByteArrayOutputStream capturedLogs = captureLogs();

        TimingFilter ourTimingFilter = new TimingFilter();
        ourTimingFilter.doFilterInternal(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mock(FilterChain.class));

        String logs = capturedLogs.toString();
        assertThat(logs).containsPattern("Total Execution time took [0-9]* ms");
    }

    private ByteArrayOutputStream captureLogs() {
        String consoleLoggingPattern = "Message is: %m%n";

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(consoleLoggingPattern);
        ple.setContext(lc);
        ple.start();

        ByteArrayOutputStream capturedLogs = new ByteArrayOutputStream();
        OutputStreamAppender<ILoggingEvent> logAppender = new OutputStreamAppender<>();
        logAppender.setEncoder(ple);
        logAppender.setContext(lc);
        logAppender.setOutputStream(capturedLogs);
        logAppender.start();

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(logAppender);
        ((Logger) LoggerFactory.getLogger(TimingFilter.class)).setLevel(Level.DEBUG);

        return capturedLogs;
    }
}
