package com.ford.turbo.aposb.common.authsupport.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.ford.turbo.aposb.common.basemodels.controller.exception.InvalidVinException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VinValidatorTest {

	private VinValidator validator;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void beforeEachTest() {
		validator = new VinValidator();
	}
	
	@Test
	public void shouldThrowException_ifVinIsNull() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN () must be 17 uppercase alphanumeric characters");
		
		validator.validateVIN(null, true);
	}
	
	@Test
	public void shouldThrowInvalidVinException_WhenVinOfLength16Chars() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (1234567890123546) must be 17 uppercase alphanumeric characters");
		
		validator.validateVIN("1234567890123546", true);
	}
	
	@Test
	public void shouldThrowInvalidVinException_WhenVinOfLength18Chars() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (123456789012354678) must be 17 uppercase alphanumeric characters");
		
		validator.validateVIN("123456789012354678", true);
	}
	
	@Test
	public void shouldThrowInvalidVinException_WhenVinhavingLowerCase() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (2c3CDXBG9DH624601) must be 17 uppercase alphanumeric characters");
		
		validator.validateVIN("2c3CDXBG9DH624601", true);
	}
	
	@Test
	public void shouldThrowInvalidVinException_WhenVinhavingSpecialCharacters() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (2C3CDXBG9DH6246*1) must be 17 uppercase alphanumeric characters");
		
		validator.validateVIN("2C3CDXBG9DH6246*1", true);
	}
	
	@Test
	public void shouldThrowException_ifVinHasCharacter_I() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (1234567890123456I) has I, O, Q as one of the characters");
		
		validator.validateVIN("1234567890123456I", true);
	}
	
	@Test
	public void shouldThrowException_ifVinHasCharacter_O() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (1234567890123456O) has I, O, Q as one of the characters");
		
		validator.validateVIN("1234567890123456O", true);
	}
	
	@Test
	public void shouldThrowException_ifVinHasCharacter_Q() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (1234567890123456Q) has I, O, Q as one of the characters");
		
		validator.validateVIN("1234567890123456Q", true);
	}
	
	@Test
	public void shouldNotThrowException_ifVinHas17AlphaNumeric_whenBegins3() {
		validator.validateVIN("3ABCDEFGHJKLMNPRS", true);
	}
	
	@Test
	public void shouldThrowException_whenVinBeginsWith1_andVins9thCharacterIsNot_0To9_orX() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (1S1GT73AZV2123717) 9th character is invalid");
		
		validator.validateVIN("1S1GT73AZV2123717", true);
	}
	
	@Test
	public void shouldNotThrowException_WhenValidateVinCalled_WithVins9thCharacterSameAsRemainder_10_whenVinBeginsWith1() {
		validator.validateVIN("1S1GT73AXV2123717", true);
	}
	
	@Test
	public void shouldThrowException_whenVinBeginsWith1_andWegihtSumIsNotEqualTo_9thCharacter() {
		expectedException.expect(InvalidVinException.class);
		expectedException.expectMessage("VIN (2C3CDXBG6DH624601) failed 9th digit check");
		
		validator.validateVIN("2C3CDXBG6DH624601", true);
	}
	
	@Test
	public void creationOfObjectShouldPrepopulateInitialData() {
		Map<Character, Integer> transliterationMap = validator.getTransliterationMap();
		Map<Integer, Integer> weightMapPerPosition = validator.getWeightMapPerPosition();
		
		assertTransliterationMap(transliterationMap);
		assertWeightMapPerPosition(weightMapPerPosition);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowException_whenModifyingTranslationMap() {
		Map<Character, Integer> transliterationMap = validator.getTransliterationMap();
		
		transliterationMap.put('Z', 1);
	}
	
	@Test
	public void shouldNotPerform_IOQCheck_whenExtendedCheckFalse() {
		validator.validateVIN("IC3CDXBG6DH624601", false);
	}
	
	@Test
	public void shouldNotPerform_WeightProductsCheck_whenExtendedCheckFalse() {
		validator.validateVIN("1C3CDXBG6DH624601", false);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowException_whenModifyingWeightMap() {
		Map<Integer, Integer> weightMapPerPosition = validator.getWeightMapPerPosition();
		
		weightMapPerPosition.put(1, 1);
	}
	
	private void assertWeightMapPerPosition(Map<Integer, Integer> weightMapPerPosition) {
		assertEquals(17, weightMapPerPosition.size());
		
		for(int i = 1; i < 18; i++) {
			assertTrue(weightMapPerPosition.get(i) != null);
		}
	}

	private void assertTransliterationMap(Map<Character, Integer> transliterationMap) {
		assertEquals(23, transliterationMap.size());
		
		for(char c = 'A'; c <= 'Z'; ++c) {
			if(c != 'O' && c != 'I' && c != 'Q')
				assertTrue(transliterationMap.get(c) != null);
		}
	}
}
