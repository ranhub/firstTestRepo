package com.ford.turbo.aposb.common.authsupport.environment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExpressionParserTest {

	ExpressionParser expressionParser;

	@Before
	public void setup() throws Exception {
		expressionParser = Mockito.spy(new ExpressionParser());
	}
	
	@Test
	public void should_returnCorrectParsedExpression_withEnvPlaceholders() {
		when(expressionParser.getEnvValue("ENV1")).thenReturn("VALUE1");
		when(expressionParser.getEnvValue("ENV2")).thenReturn("$\"VALUE2\"$"); //special characters
		
		String expected = "--VALUE1--$\"VALUE2\"$--\nVALUE1--{[ENV:DOES NOT EXIST]}--";
		assertEquals(expected, expressionParser.parse("--{[ENV:ENV1]}--{[ENV:ENV2]}--\n{[ENV:ENV1]}--{[ENV:DOES NOT EXIST]}--"));
	}

	@Test
	public void should_returnCorrectParsedExpression_withFilePlaceholders() {
		when(expressionParser.getFileContents("FILE1")).thenReturn("FILE1-CONTENTS");
		when(expressionParser.getFileContents("FILE2")).thenReturn("$\"FILE2-CONTENTS\"$");
		
		String expected = "--FILE1-CONTENTS--$\"FILE2-CONTENTS\"$--\nFILE1-CONTENTS--{[FILE:DOES NOT EXIST]}--";
		assertEquals(expected, expressionParser.parse("--{[FILE:FILE1]}--{[FILE:FILE2]}--\n{[FILE:FILE1]}--{[FILE:DOES NOT EXIST]}--"));
	}
	
	@Test
	public void should_returnCorrectParsedExpression_withGlobalFilePlaceholder() {
		when(expressionParser.getFileContents("FILE1")).thenReturn("FILE1-CONTENTS");
		
		String expected = "FILE1-CONTENTS";
		assertEquals(expected, expressionParser.parse("{}//FILE:FILE1"));
	}
	
	@Test
	public void should_returnOrderProcessingAndReferenceToOtherPlaceholders() {
		when(expressionParser.getFileContents("FILE1")).thenReturn("--{[FILE:FILE2]}--{[ENV:ENV1]}--");
		when(expressionParser.getFileContents("FILE2")).thenReturn("--{[ENV:ENV2]}--");
		when(expressionParser.getEnvValue("ENV1")).thenReturn("VALUE1");
		when(expressionParser.getEnvValue("ENV2")).thenReturn("VALUE2");
		
		String expected = "----VALUE2----VALUE1--";
		assertEquals(expected, expressionParser.parse("{}//FILE:FILE1"));
	}
}
