package com.ford.turbo.servicebooking.utils;

import static java.nio.file.Files.readAllBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.interceptors.HttpTraceInterceptor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utilities {

	public static <T extends Object> T getJsonFileData(String filename, Class<T> type){

		Resource resource = new ClassPathResource(filename);
		String jsonString = null;
		T data = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		try {
			jsonString = new String(readAllBytes(resource.getFile().toPath()), "UTF8");
		} catch (UnsupportedEncodingException e) {
			log.error("Caught UnsupportedEncodingException: ", e);
		} catch (IOException e) {
			log.error("Caught IOException: ", e);
		}

		try {
			data = mapper.readValue(jsonString, type);
		} catch (JsonParseException e) {
			log.error("Caught JSONParseException: ", e);
		} catch (JsonMappingException e) {
			log.error("Caught JsonMappingException: ", e);
		} catch (IOException e) {
			log.error("Caught IOException: ", e);
		}

		return data;
	}
	
	public static <T extends TimedHystrixCommand<?>> ByteArrayOutputStream getLogContent(String consoleLoggingPattern) {
		return getLogContent(consoleLoggingPattern, null);
	}
	
	public static <T extends TimedHystrixCommand<?>> ByteArrayOutputStream getLogContent(String consoleLoggingPattern,
			Class<T> type) {
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
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.ERROR);
		if(type!=null) {
			((Logger) LoggerFactory.getLogger(type)).setLevel(Level.DEBUG);
		}
		((Logger) LoggerFactory.getLogger(HttpTraceInterceptor.class)).setLevel(Level.INFO);
		((Logger) LoggerFactory.getLogger(Utilities.class)).setLevel(Level.DEBUG);

		return capturedLogs;
	}

	public static void populateRequestTraceForCommand(HttpHeaders headers, TimedHystrixCommand<?> command) {
		String traceId = String.valueOf(command.getTraceInfo().getTracer().getCurrentSpan().getTraceId());
		String spanID = String.valueOf(command.getTraceInfo().getTracer().getCurrentSpan().getSpanId());
		
		headers.set("X-B3-TraceId", traceId);
		headers.set("X-B3-SpanId", spanID);
	}
	
	public static HashMap<String, String> getWeekDayHashMap() {
		HashMap<String, String> weekDayMap = new HashMap<>();
		weekDayMap.put("monday", StringUtils.EMPTY);
		weekDayMap.put("tuesday",StringUtils.EMPTY);
		weekDayMap.put("wednesday",StringUtils.EMPTY);
		weekDayMap.put("thursday",StringUtils.EMPTY);
		weekDayMap.put("friday",StringUtils.EMPTY);
		weekDayMap.put("saturday",StringUtils.EMPTY);
		weekDayMap.put("sunday",StringUtils.EMPTY);
		return weekDayMap;
	}
}
