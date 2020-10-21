package com.ford.turbo.aposb.common.authsupport.data;

import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApplicationId {
	private final String id;
	private final ContinentCode region;
	private final Classifier classifier;

	public enum Classifier {
		AUTHENTICATED, UNAUTHENTICATED
	}
}
