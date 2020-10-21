package com.ford.turbo.aposb.common.authsupport.environment;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionParser {
	
	final static Pattern ENV_PLACEHOLDER = Pattern.compile("\\{\\[ENV:(.*?)\\]\\}");
	final static Pattern FILE_PLACEHOLDER = Pattern.compile("\\{\\[FILE:(.*?)\\]\\}");
	
	public String parse(String expression) {
		return processEnvPlaceholders(processFilePlaceholders(expression));
	}

	protected String getEnvValue(String name) {
		return System.getenv(name);
	}
	
	protected String getFileContents(String filename) {
		try {
			return IOUtils.toString(new FileInputStream(filename), "UTF-8");
		} catch (Exception e) {
			log.warn("Unable to read file " + filename + " - cause: " + e.getMessage());
			return null;
		}
	}
	
	protected String processFilePlaceholders(String vcap) {
		StringBuffer sb = new StringBuffer();
		if (vcap == null) {
			return sb.toString();
		}
		// replace entire vcap with file
		String entireVCAPServicesFilePrefix = "{}//FILE:";
		if (vcap.startsWith(entireVCAPServicesFilePrefix)) {
			String fileContents = getFileContents(vcap.substring(entireVCAPServicesFilePrefix.length()));
			if (fileContents == null) {
		    	log.warn("Unable to replace the following entire file placeholder (missing file?): " + vcap);
			} else {
				vcap = fileContents;
			}
		}
		
		// replace individual embedded file placeholders
		Matcher matcher = FILE_PLACEHOLDER.matcher(vcap);
		while (matcher.find()) {
		    String fileContents = getFileContents(matcher.group(1));
		    if (fileContents == null) {
		    	log.warn("Unable to replace the following file placeholder (missing file?): " + matcher.group(0));
		    	fileContents = matcher.group(0);
		    }
			matcher.appendReplacement(sb, Matcher.quoteReplacement(fileContents));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	protected String processEnvPlaceholders(String vcap) {
		Matcher matcher = ENV_PLACEHOLDER.matcher(vcap);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
		    String envValue = getEnvValue(matcher.group(1));
		    if (envValue == null) {
		    	log.warn("Unable to replace the following env placeholder (missing env variable): " + matcher.group(0));
		    	envValue = matcher.group(0);
		    }

			matcher.appendReplacement(sb, Matcher.quoteReplacement(envValue));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
