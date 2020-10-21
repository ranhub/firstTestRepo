package com.ford.turbo.aposb.common.basemodels.hystrix;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TimedHystrixCommandTest {

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;

    @Before
    public void setup() {
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());
    }

    @Test
    public void should_logExecutionTime(){
        ByteArrayOutputStream capturedLogs = captureLogs();

        DummyCommand dummyCommand = new DummyCommand(traceInfo, HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("DUMMY")));
        dummyCommand.execute();

        String logs = capturedLogs.toString();
        assertThat(logs).containsPattern("Execution time: command=DummyCommand, groupKey=DUMMY, time=[0-9]* ms");
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
        ((Logger) LoggerFactory.getLogger(DummyCommand.class)).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger(TimedHystrixCommand.class)).setLevel(Level.DEBUG);

        return capturedLogs;
    }

    private class DummyCommand extends TimedHystrixCommand {

        public DummyCommand(TraceInfo traceInfo, Setter setter) {
            super(traceInfo, setter);
        }

        @Override
        public Object doRun() throws Exception {
            return "I ran today";
        }
    }
}
