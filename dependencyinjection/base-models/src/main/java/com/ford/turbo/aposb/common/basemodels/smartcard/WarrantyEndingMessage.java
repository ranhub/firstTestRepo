package com.ford.turbo.aposb.common.basemodels.smartcard;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarrantyEndingMessage extends BaseMessage implements Serializable {

	private static final long serialVersionUID = 1L;
    @NotNull
	private String warrantyEndingDate;
	
	@Override
	public String toString() {
		return super.toString() 
				+ " warrantyEndingDate="+warrantyEndingDate;
	}
}
