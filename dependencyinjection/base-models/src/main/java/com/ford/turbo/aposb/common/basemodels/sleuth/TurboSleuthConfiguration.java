package com.ford.turbo.aposb.common.basemodels.sleuth;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TurboSleuthConfiguration {

    /**
     * Tags we copy from the current span into the MDC so they can be in each log entry.
     */
    @Value("${turbo.sleuth.log.slf4j.mdcTags:}")
    private List<String> mdcTags;

    /**
     * Pattern for URLs that should be skipped in tracing
     */
    @Value("${spring.sleuth.web.skipPattern:}")
    private String skipPattern;

    @Bean
    @Primary
    public SpanLogger slf4jSpanLogger() {
        return new ConfigurableSlf4jSpanLogger("", mdcTags);
    }

    @Bean
    @Primary
    public SpanExtractor<HttpServletRequest> servletRequestSpanExtractor(Random random) {
        return new ConfigurableSpanExtractor(random, Pattern.compile(skipPattern), mdcTags);
    }

    @Bean
    @Primary
    public DefaultTracer configurableTracer(
            Sampler defaultSampler,
            Random random,
            SpanNamer spanNamer,
            SpanLogger spanLogger,
            SpanReporter spanReporter,
            TraceKeys traceKeys) {
        return new ConfigurableTracer(defaultSampler, random, spanNamer, spanLogger, spanReporter, mdcTags, traceKeys);
    }

    @Bean
    public TraceInfo traceInfo(DefaultTracer tracer, TraceKeys traceKeys) {
        return new TraceInfo(tracer, traceKeys);
    }
}
