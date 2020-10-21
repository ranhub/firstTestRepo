package com.ford.turbo.servicebooking.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DealerCodeFormatter {

	public static List<String> formatDealerCodes(List<String> dealerCodes) {
		List<String> updatedDealerCodes = new ArrayList<>();
		for (String dealerCode : dealerCodes) {
			if (!StringUtils.isEmpty(dealerCode)) {
				updatedDealerCodes.add(formatDealerCode(dealerCode));
			}
		}
		return updatedDealerCodes;
	}

	public static String formatDealerCode(String dealerCode) {
		return dealerCode.matches("^[a-zA-Z]{2}.*") ? dealerCode.substring(2) : dealerCode;
	}
}
