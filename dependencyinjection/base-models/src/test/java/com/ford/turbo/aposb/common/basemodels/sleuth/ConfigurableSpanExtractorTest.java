package com.ford.turbo.aposb.common.basemodels.sleuth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurableSpanExtractorTest {

    @Mock
    Random mockRandom;

    @Test
    public void should_generateTraceId_when_traceIdHeaderNotPresentInRequest() {
        when(mockRandom.nextLong()).thenReturn(42L);

        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(mockRandom, Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.getTraceId()).isEqualTo(42L);
    }

    @Test
    public void should_generateSpanId_when_spanIdHeaderNotPresentInRequest() {
        when(mockRandom.nextLong()).thenReturn(42L);

        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(mockRandom, Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.getSpanId()).isEqualTo(42L);
    }

    @Test
    public void should_generateMatchingSpanIdAndTraceId_when_spanIdAndTraceIdHeadersNotPresentInRequest() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(new Random(), Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.getTraceId()).isEqualTo(span.getSpanId());
    }

    @Test
    public void should_extractTrace_when_traceIdHeaderPresentInRequest() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .header("X-B3-TraceId", "c0dedbaddddddddd")
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(new Random(), Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.getTraceId()).isEqualTo(0xc0dedbadddddddddL);
    }

    @Test
    public void should_extractSpan_when_spanIdHeaderPresentInRequest() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .header("X-B3-SpanId", "c0dedbaddddddddd")
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(new Random(), Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.getSpanId()).isEqualTo(0xc0dedbadddddddddL);
    }

    @Test
    public void should_notExtractCustomHeader_when_notConfigured() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .header("X-Custom-Trace-Info", "i like turtles")
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(new Random(), Pattern.compile("skip-nothing"), Collections.emptyList());

        Span span = extractor.joinTrace(req);

        assertThat(span.tags()).isEmpty();
    }

    @Test
    public void should_extractCustomHeader_when_configured() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .header("X-Custom-Trace-Info", "i like turtles")
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(
                        new Random(),
                        Pattern.compile("skip-nothing"),
                        Collections.singletonList("http.x-custom-trace-info"));

        Span span = extractor.joinTrace(req);

        assertThat(span.tags().keySet()).containsOnly("http.x-custom-trace-info");
        assertThat(span.tags().get("http.x-custom-trace-info")).isEqualTo("i like turtles");
    }

    @Test
    public void should_extractManyCustomHeaders_when_configured() {
        MockHttpServletRequest req =
                MockMvcRequestBuilders.get(URI.create("http://foo"))
                        .header("X-One", "1")
                        .header("X-Two", "2")
                        .header("X-Three", "3")
                        .header("X-Four", "4")
                        .buildRequest(new MockServletContext());

        ConfigurableSpanExtractor extractor =
                new ConfigurableSpanExtractor(new Random(),
                        Pattern.compile("skip-nothing"),
                        Arrays.asList("http.x-one", "http.x-four"));

        Span span = extractor.joinTrace(req);

        assertThat(span.tags().keySet()).containsOnly("http.x-one", "http.x-four");
        assertThat(span.tags().get("http.x-one")).isEqualTo("1");
        assertThat(span.tags().get("http.x-four")).isEqualTo("4");
    }


}
