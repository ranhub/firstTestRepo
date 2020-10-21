package com.ford.turbo.aposb.common.basemodels.smartcard;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String eventType;
	private String eventId;
	private String userGuid;
	private String vin;
	private String originator;
	private String publisher;
	private String originationTimestamp;
	
//	public BaseMessage eventType(String eventType) {
//		this.eventType = eventType;
//		return this;
//	}
//	
//	public BaseMessage eventId(String eventId) {
//		this.eventId = eventId;
//		return this;
//	}
//	
//	public BaseMessage userGuid(String userGuid) {
//		this.userGuid = userGuid;
//		return this;
//	}
//	
//	public BaseMessage vin(String vin) {
//		this.vin = vin;
//		return this;
//	}
//	
//	public BaseMessage originator(String originator) {
//		this.originator = originator;
//		return this;
//	}
//	
//	public BaseMessage publisher(String publisher) {
//		this.publisher = publisher;
//		return this;
//	}
//	
//	public BaseMessage originationTimestamp(String originationTimestamp) {
//		this.originationTimestamp = originationTimestamp;
//		return this;
//	}
	

}
