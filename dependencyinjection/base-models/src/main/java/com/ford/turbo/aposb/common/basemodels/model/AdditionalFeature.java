package com.ford.turbo.aposb.common.basemodels.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdditionalFeature implements Serializable{
	private String code;
	private String description;
}
