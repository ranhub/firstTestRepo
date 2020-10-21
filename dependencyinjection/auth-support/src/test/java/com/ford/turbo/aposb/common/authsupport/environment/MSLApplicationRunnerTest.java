package com.ford.turbo.aposb.common.authsupport.environment;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class MSLApplicationRunnerTest {

	@Test
	public void should_returnCorrectOrderedSpaceProfileList_whenMSLNAPreProdSpaceIsUsed() throws Exception {
		List<String> actual = MSLApplicationRunner.getAdditionalProfilesForSpaceEnvironment("MSL_US_West_Stage_FordSvcs");
		List<String> expected = Arrays.asList("all", "us", "us-west", "stage", "stage-us", "stage-us-west");
		assertEquals(expected, actual);
	}
	
	@Test
	public void should_returnCorrectOrderedSpaceProfileList_whenMSLChinaPreProdSpaceIsUsed() throws Exception {
		List<String> actual = MSLApplicationRunner.getAdditionalProfilesForSpaceEnvironment("MSL_CN_North_Perf_FordSvcs");
		List<String> expected = Arrays.asList("all", "cn", "cn-north", "perf", "perf-cn", "perf-cn-north");
		assertEquals(expected, actual);
	}
	
	@Test
	public void should_returnCorrectOrderedSpaceProfileList_whenMSLNAProdSpaceIsUsed() throws Exception {
		List<String> actual = MSLApplicationRunner.getAdditionalProfilesForSpaceEnvironment("Prod_US_East_FordSvcs");
		List<String> expected = Arrays.asList("all", "us", "us-east", "prod", "prod-us", "prod-us-east");
		assertEquals(expected, actual);
	}
	
	@Test
	public void should_returnCorrectOrderedSpaceProfileList_whenMSLChinaProdSpaceIsUsed() throws Exception {
		List<String> actual = MSLApplicationRunner.getAdditionalProfilesForSpaceEnvironment("MSL_CN_East_Prod_FordSvcs");
		List<String> expected = Arrays.asList("all", "cn", "cn-east", "prod", "prod-cn", "prod-cn-east");
		assertEquals(expected, actual);
	}
	
}
