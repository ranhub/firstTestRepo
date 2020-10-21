package com.ford.turbo.aposb.common.authsupport.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VinValidator {
	
	private static final String VIN_17_CHARS_CAPS_ALPHANUMERIC = "[A-Z0-9]{17}";
	private static final String VIN_IOQ = "[^IOQ]{17}";
	
	private Map<Character, Integer> transliterationMap;
	private Map<Integer, Integer> weightMapPerPosition;

	@Autowired
	public VinValidator() {
		populateTransliterationInfo();
		populateWeightPerPosition();
	}
	
	public void validateVIN(String vin, boolean extendedCheck) {
		if(vin == null) {
			throw new InvalidVinException(vin);
		}
		
		if (!vin.matches(VIN_17_CHARS_CAPS_ALPHANUMERIC)) {
            throw new InvalidVinException(vin);
        }

		if(extendedCheck) {
			if (!vin.matches(VIN_IOQ)) {
	            throw new InvalidVinException(vin, 1005, "VIN (" + vin + ") has I, O, Q as one of the characters");
	        }
			
			// US=1, CAN=2
			if(vin.charAt(0) == '1' || vin.charAt(0) == '2') {
				if(!validate9thCharacter(vin)) {
					throw new InvalidVinException(vin, 1005, "VIN (" + vin + ") 9th character is invalid");
				}
				
				if(!validateWeightedSum(vin)) {
					log.info("VIN (" + vin + ") failed weight sum check");
					throw new InvalidVinException(vin, 1005, "VIN (" + vin + ") failed 9th digit check");
				}
			}
		}
	}
	
	private boolean validate9thCharacter(String vin) {
		if(Character.isDigit(vin.charAt(8)) || vin.charAt(8) == 'X') {
			return true;
		}
		return false;
	}

	private void populateTransliterationInfo() {
		Map<Character, Integer> transliterationMap = new HashMap<>();
		
		transliterationMap.put('A', 1);
		transliterationMap.put('B', 2);
		transliterationMap.put('C', 3);
		transliterationMap.put('D', 4);
		transliterationMap.put('E', 5);
		transliterationMap.put('F', 6);
		transliterationMap.put('G', 7);
		transliterationMap.put('H', 8);

		transliterationMap.put('J', 1);
		transliterationMap.put('K', 2);
		transliterationMap.put('L', 3);
		transliterationMap.put('M', 4);
		transliterationMap.put('N', 5);
		transliterationMap.put('P', 7);
		transliterationMap.put('R', 9);

		transliterationMap.put('S', 2);
		transliterationMap.put('T', 3);
		transliterationMap.put('U', 4);
		transliterationMap.put('V', 5);
		transliterationMap.put('W', 6);
		transliterationMap.put('X', 7);
		transliterationMap.put('Y', 8);
		transliterationMap.put('Z', 9);
		
		this.transliterationMap = Collections.unmodifiableMap(transliterationMap);
	}
	
	private void populateWeightPerPosition() {
		Map<Integer, Integer> weightMapPerPosition = new HashMap<>();
		
		weightMapPerPosition.put(1, 8);
		weightMapPerPosition.put(2, 7);
		weightMapPerPosition.put(3, 6);
		weightMapPerPosition.put(4, 5);
		weightMapPerPosition.put(5, 4);
		weightMapPerPosition.put(6, 3);
		weightMapPerPosition.put(7, 2);
		weightMapPerPosition.put(8, 10);
		weightMapPerPosition.put(9, 0);
		weightMapPerPosition.put(10, 9);
		weightMapPerPosition.put(11, 8);
		weightMapPerPosition.put(12, 7);
		weightMapPerPosition.put(13, 6);
		weightMapPerPosition.put(14, 5);
		weightMapPerPosition.put(15, 4);
		weightMapPerPosition.put(16, 3);
		weightMapPerPosition.put(17, 2);
		
		this.weightMapPerPosition = Collections.unmodifiableMap(weightMapPerPosition);
	}
	
	public Map<Character, Integer> getTransliterationMap() {
		return transliterationMap;
	}

	public Map<Integer, Integer> getWeightMapPerPosition() {
		return weightMapPerPosition;
	}
	
	private boolean validateWeightedSum(String vin) {
		int sum = calculateSumOfWeightedProducts(vin);
		int remainder = sum % 11;
		
		char charAtPosition9 = vin.charAt(8);
		char remainderChar = remainder == 10 ? 'X' : (char)(remainder + '0');
		
		return remainderChar == charAtPosition9;
	}
	
	private int calculateSumOfWeightedProducts(String vin) {
		char [] vinCharacters = vin.toCharArray();
		int sum = 0;
		int currentCharacterPosition = 1;
		
		for(char ch: vinCharacters) {
			int translatedValue;
			if(Character.isDigit(ch)) {
				// Gets the integer representation of the digit
				translatedValue = ch - '0';
			} else {
				translatedValue = transliterationMap.get(ch);
			}
			
			int weightOfCurrentCharacter = translatedValue * weightMapPerPosition.get(currentCharacterPosition);
			sum += weightOfCurrentCharacter;
			currentCharacterPosition++;
		}
		
		return sum;
	}
}
