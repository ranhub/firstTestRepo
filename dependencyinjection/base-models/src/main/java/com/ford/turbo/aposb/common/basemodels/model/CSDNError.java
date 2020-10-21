package com.ford.turbo.aposb.common.basemodels.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CSDNError {
	public String statusContext;
	public String statusCode;
	public String message;
}