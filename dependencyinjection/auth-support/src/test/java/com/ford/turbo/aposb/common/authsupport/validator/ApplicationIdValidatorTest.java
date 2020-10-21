package com.ford.turbo.aposb.common.authsupport.validator;

import java.util.Arrays;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AppIdNotFoundException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationIdValidatorTest {
	private ApplicationIdValidator applicationIdValidator;
	private String appId = "app-id";
	
	@Mock
	private ContinentCodeExtractor continentCodeExtractor;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void beforeEachTest() {
		applicationIdValidator = new ApplicationIdValidator(continentCodeExtractor);
	}
	
	@Test(expected = AppIdNotFoundException.class)
	public void checkEmpty_shouldThrowException_whenAppidNull() {
		applicationIdValidator.checkEmpty(null);
	}
	
	@Test(expected = AppIdNotFoundException.class)
	public void checkEmpty_shouldThrowException_whenAppidEmpty() {
		applicationIdValidator.checkEmpty("");
	}
	
	@Test
	public void checkEmpty_shouldNotThrowException_whenAppidNotEmpty() {
		applicationIdValidator.checkEmpty(appId);
	}
	
	@Test
	public void checkValidAppId_shouldThrowException_whenContinentCodeIsNull() {
		exception.expect(UnknownAppIdException.class);
		exception.expectMessage("Unknown application id " + appId);

		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(null);
		
		applicationIdValidator.checkValidAppId(appId);
	}
	
	@Test
	public void checkValidAppId_shouldNotThrowException_whenContinentCodeIsAvaialble() {
		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(ContinentCode.NA);
		
		applicationIdValidator.checkValidAppId(appId);
		Mockito.verify(continentCodeExtractor).getContinent(appId);
	}
	
	@Test
	public void checkValidAppId_shouldCall_checkEmpty() {
		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(ContinentCode.NA);
		
		ApplicationIdValidator spy = Mockito.spy(applicationIdValidator);
		
		spy.checkValidAppId(appId);
		Mockito.verify(spy).checkEmpty(appId);
	}
	
	@Test
	public void checkValidRegionalAppId_shouldNotThrowException_andVerify() {
		ApplicationIdValidator spy = Mockito.spy(applicationIdValidator);
		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(ContinentCode.NA);
		
		spy.checkValidRegionalAppId(appId, Arrays.asList(ContinentCode.NA));
		Mockito.verify(spy).checkEmpty(appId);
		Mockito.verify(continentCodeExtractor).getContinent(appId);
	}
	
	@Test(expected = NoBackendAvailableException.class)
	public void checkValidRegionalAppId_shouldThrowException() {
		ApplicationIdValidator spy = Mockito.spy(applicationIdValidator);
		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(ContinentCode.AP);
		
		spy.checkValidRegionalAppId(appId, Arrays.asList(ContinentCode.NA));
	}
	
	@Test(expected = UnknownAppIdException.class)
	public void checkValidRegionalAppId_shouldThrowException_whenContinentCodeNull() {
		Mockito.when(continentCodeExtractor.getContinent(appId))
			.thenReturn(null);
		
		applicationIdValidator.checkValidRegionalAppId(appId, Arrays.asList(ContinentCode.NA));
	}
}
