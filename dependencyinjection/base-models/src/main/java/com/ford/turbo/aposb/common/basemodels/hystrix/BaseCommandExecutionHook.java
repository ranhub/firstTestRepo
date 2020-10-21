package com.ford.turbo.aposb.common.basemodels.hystrix;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadGatewayException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadRequestException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.CircuitBreakerOpenException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.GatewayTimeoutException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.HasFordError;
import com.ford.turbo.aposb.common.basemodels.controller.exception.HttpStatusException;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;


public class BaseCommandExecutionHook extends HystrixCommandExecutionHook {

    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, HystrixRuntimeException.FailureType failureType, Exception e) {
    	boolean printOnlyFirstElement = true;
        String commandName = commandInstance.getClass().getSimpleName();
        Logger logger = LoggerFactory.getLogger(commandName);
        logger.error("Failed due to " +failureType.name() +" "+ e.getMessage());

        Exception exception = e;
        HasFordError hasFordError = HasFordError.extractFrom(exception);
        if (hasFordError == null) {
            if (failureType == HystrixRuntimeException.FailureType.SHORTCIRCUIT) {
            	printOnlyFirstElement = false;
                exception = new CircuitBreakerOpenException(commandInstance, e); // 503
            } else if (failureType == HystrixRuntimeException.FailureType.TIMEOUT) {
            	printOnlyFirstElement = false;
                exception = new GatewayTimeoutException(commandInstance, e); // 504
            }else if (hasCause(e, HttpClientErrorException.class)) {
            	printOnlyFirstElement = false;
                HttpClientErrorException ex = (HttpClientErrorException) findCause(e, HttpClientErrorException.class);
                logger.error("Error Message Body " +ex.getResponseBodyAsString());
                exception = new BadRequestException(e, ex.getStatusCode()); // 4xx
            } else if (failureType == HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION) {
                exception = new BadRequestException(e); // 400
            } else if (hasCause(e, HystrixBadRequestException.class)) {
                exception = new BadRequestException(e); // 400
            }
            // bad gateway
            else if (hasCause(e, HttpServerErrorException.class)) {
            	printOnlyFirstElement = false;
            	HttpServerErrorException ex = (HttpServerErrorException) findCause(e, HttpServerErrorException.class);
            	logger.error("Error Message Body " +ex.getResponseBodyAsString());
                exception = new BadGatewayException(commandName + " threw " + e.getClass().getSimpleName() + " - " + e.getMessage(), e); // 502 - server problems
            } else if (hasCause(e, SoapFaultClientException.class)) {
                exception = new BadGatewayException(e); // 502 - bad soap
            } else if (hasCause(e, XmlMappingException.class)) {
                exception = new BadGatewayException(e); // 502 - bad xml from soap
            } else if (hasCause(e, JsonProcessingException.class)) {
                exception = new BadGatewayException(e); // 502 - bad json
            } else if (hasCause(e, CertificateException.class)) {
                exception = new BadGatewayException(e); // 502 - bad endpoint
            } else if (hasCause(e, SSLException.class)) {
                exception = new BadGatewayException(e); // 502 - bad endpoint
            } else if (hasCause(e, SocketTimeoutException.class)) {
                exception = new HttpStatusException(HttpStatus.GATEWAY_TIMEOUT, e); // 504 - server taking too long
            }
            
            if(printOnlyFirstElement)
            	printStackTraceElement(e, logger);
        }

        return super.onError(commandInstance, failureType, exception);
    }

    protected void printStackTraceElement(Exception ex, Logger logger ) {
         
    	StackTraceElement[] stackTrace = ex.getStackTrace();
    	if(stackTrace != null && stackTrace.length > 0 && stackTrace[0] != null) {
			StackTraceElement element = stackTrace[0];
			logger.error("FordError:"+ ex.getMessage() + " failed:" + ex.getClass().getSimpleName() + " method:"+element.getMethodName()+ " line:"+element.getLineNumber()+ " class:" + element.getClassName());
		}else {
			logger.error("FordError:"+ ex.getMessage() + " with:" + ex.getClass().getSimpleName());
		}
    }
    
    protected boolean hasCause(Throwable t, Class<? extends Throwable> type) {
        return (findCause(t, type) != null);
    }

    protected Throwable findCause(Throwable t, Class<? extends Throwable> type) {
        Throwable cause = t;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return cause;
            }
            cause = cause.getCause();
        }

        return null;
    }
}
