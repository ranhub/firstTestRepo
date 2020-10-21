package com.ford.turbo.aposb.common.basemodels.sleuth;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public class ConfigurableSpanExtractor implements SpanExtractor<HttpServletRequest> {

    private static final String HTTP_COMPONENT = "http";

    private final Random random;
    private final Pattern skipPattern;
    private List<String> headersToTag;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public ConfigurableSpanExtractor(Random random, Pattern skipPattern, List<String> headersToTag) {
        this.random = Objects.requireNonNull(random);
        this.skipPattern = Objects.requireNonNull(skipPattern);
        this.headersToTag = Objects.requireNonNull(headersToTag);
    }

    /**
     * Note that this is different from the built-in Sleuth extractor, which returns null when
     * the request has no trace id. We need to choose a trace ID so we can return a span that contains
     * the custom tags from the request as well. Essentially, we're telling Sleuth that all incoming
     * requests are part of a trace in progress. This should be harmless.
     */
    @Override
    public Span joinTrace(HttpServletRequest carrier) {

        String uri = this.urlPathHelper.getPathWithinApplication(carrier);
        boolean skip = this.skipPattern.matcher(uri).matches()
                || Span.SPAN_NOT_SAMPLED.equals(carrier.getHeader(Span.SAMPLED_NAME));

        long newId = this.random.nextLong();
        long traceId = carrier.getHeader(Span.TRACE_ID_NAME) != null
                ? Span.hexToId(carrier.getHeader(Span.TRACE_ID_NAME))
                : newId;
        long spanId = carrier.getHeader(Span.SPAN_ID_NAME) != null
                ? Span.hexToId(carrier.getHeader(Span.SPAN_ID_NAME))
                : newId;
        return buildParentSpan(carrier, uri, skip, traceId, spanId);
    }

    private Span buildParentSpan(HttpServletRequest carrier, String uri, boolean skip,
                                 long traceId, long spanId) {
        Span.SpanBuilder span = Span.builder().traceId(traceId).spanId(spanId);
        String processId = carrier.getHeader(Span.PROCESS_ID_NAME);
        String parentName = carrier.getHeader(Span.SPAN_NAME_NAME);
        if (StringUtils.hasText(parentName)) {
            span.name(parentName);
        } else {
            span.name(HTTP_COMPONENT + ":" + "/parent" + uri);
        }
        if (StringUtils.hasText(processId)) {
            span.processId(processId);
        }
        if (carrier.getHeader(Span.PARENT_ID_NAME) != null) {
            span.parent(Span
                    .hexToId(carrier.getHeader(Span.PARENT_ID_NAME)));
        }

        List<String> allHeaders = Collections.list(carrier.getHeaderNames());
        for (String s : headersToTag) {
            allHeaders.forEach(h -> {
                String headerKey = HTTP_COMPONENT + "." + h.toLowerCase();
                if (headerKey.equals(s)) {
                    span.tag(headerKey, carrier.getHeader(h));
                }
            });
        }
        span.remote(true);
        if (skip) {
            span.exportable(false);
        }
        return span.build();
    }
}
