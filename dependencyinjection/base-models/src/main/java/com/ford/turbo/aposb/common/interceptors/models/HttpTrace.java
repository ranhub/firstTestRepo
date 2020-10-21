package com.ford.turbo.aposb.common.interceptors.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(doNotUseGetters=true)
public class HttpTrace {
	private String method;
	private String uri;
	private List<String> requestHeaders = new ArrayList<>();
	private List<String> responseHeaders = new ArrayList<>();
	private String timeTaken;

	public HttpTrace(String uri, String method) {
		this.uri = uri;
		this.method = method;
	}

	public void addRequestHeader(String name, Object value) {
		this.requestHeaders.add(name + "=" + value);
	}

	public void addResponseHeader(String name, Object value) {
		this.responseHeaders.add(name + "=" + value);
	}
}
