package com.ford.turbo.aposb.common.authsupport.userprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class PartnerRelationship {
	private String partnerName;
	private String identifierInPartnerSystem;
}
