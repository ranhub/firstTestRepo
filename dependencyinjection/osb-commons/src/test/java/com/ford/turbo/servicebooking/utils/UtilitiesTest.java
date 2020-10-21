package com.ford.turbo.servicebooking.utils;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class UtilitiesTest {
	@Test
	public void shouldReturnFinalMapWithAllWeekdays() {
		HashMap<String, String> weekDayHashMap = Utilities.getWeekDayHashMap();
		Assert.assertEquals(7, weekDayHashMap.size());

		Collection<String> values = weekDayHashMap.keySet();

		Assert.assertTrue(values.contains("monday"));
		Assert.assertTrue(values.contains("tuesday"));
		Assert.assertTrue(values.contains("wednesday"));
		Assert.assertTrue(values.contains("thursday"));
		Assert.assertTrue(values.contains("friday"));
		Assert.assertTrue(values.contains("saturday"));
		Assert.assertTrue(values.contains("sunday"));
	}
}
