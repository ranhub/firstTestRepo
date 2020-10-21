package com.ford.turbo.aposb.common.authsupport.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Authorization {
	boolean authenticate() default true;
	ContinentCode[] regions(); 
}
