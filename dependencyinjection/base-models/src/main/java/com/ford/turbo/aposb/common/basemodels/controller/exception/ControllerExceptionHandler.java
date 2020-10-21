package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.UnknownAppIdException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // Lower values have higher priority
    private static final int DEFAULT_PRECEDENCE = 10000;

    @Order(DEFAULT_PRECEDENCE)
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
        }

        BaseResponse response = new BaseResponse();
        response.setRequestStatus(BaseResponse.RequestStatus.UNAVAILABLE);
        FordError fordError = new FordError(StatusContext.HTTP.getStatusContext(), status.value(), ex.getMessage());
        response.setError(fordError);
        
        if(!skipLoggingException(ex)) {
        	log.error("Unexpected controller exception", ex);
        }
        
        return new ResponseEntity<>(response, status);
    }

    @Order(DEFAULT_PRECEDENCE)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<?> handleUncaughtException(Exception ex) {
        final BaseResponse response = new BaseResponse();
        final HttpStatus httpStatus;
        final FordError fordError;
        final HasFordError fancyException = HasFordError.extractFrom(ex);
        
       if(!skipLoggingException(ex)) {
        	log.error("handleException", ex);
        }
        if (fancyException != null) {
            fordError = fancyException.getFordError();
            httpStatus = fancyException.getHttpStatus();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            fordError = new FordError(StatusContext.HTTP.getStatusContext(), httpStatus.value(), ex.getMessage());
        }
        response.setError(fordError);
        response.setRequestStatus(BaseResponse.RequestStatus.UNAVAILABLE);
        return new ResponseEntity<>(response, httpStatus);
    }
    
    protected boolean skipLoggingException(Exception ex) {
    	
    	// Non Command errors
    	if(HasFordError.class.isInstance(ex)) {
    		HasFordError error = (HasFordError) ex;
    		FordError fordError = error.getFordError();
    		if(fordError != null) {
				String statusContext = fordError.getStatusContext();
				log.error("FordError:"+ex.getClass().getSimpleName() + " "+ fordError.getMessage() + " with source:" 
    					+ (fordError.getSourceException() != null ? fordError.getSourceException().getSimpleName() : ""));
				printStackTraceElement(ex);
				return true;
    			 
    		}
    	}
    	// Exceptions thrown from command and may be wrapped as HystrixRuntimeException
    	else {
    		if(HasFordError.class.isInstance(ex.getCause())){
    			HasFordError error = (HasFordError) ex.getCause();
    			FordError fordError = error.getFordError();
    			log.error("FordError cause:"+ex.getClass().getSimpleName() + " "+fordError.getMessage() + " with source:" 
    					+ (fordError.getSourceException() != null ? fordError.getSourceException().getSimpleName() : ""));
    			printStackTraceElement(ex);
    			return true;
    		}
    	}
    	return isWhiteListedException(ex);
    }
    
    protected void printStackTraceElement(Exception ex) {

    	if(ex instanceof HystrixRuntimeException || ex instanceof UnknownAppIdException) {
    		// If not returned then it will print following for every command 
    		//FordError:AddressLookupCommand failed and no fallback available. failed:HystrixRuntimeException method:call line:819 class:com.netflix.hystrix.AbstractCommand$22
    		return;
    	}
    	
    	StackTraceElement[] stackTrace = ex.getStackTrace();
    	if(stackTrace != null && stackTrace.length > 0 && stackTrace[0] != null) {
			StackTraceElement element = stackTrace[0];
			log.error("FordError:"+ ex.getMessage() + " failed:" + ex.getClass().getSimpleName() + " method:"+element.getMethodName()+ " line:"+element.getLineNumber()+ " class:" + element.getClassName());
		}else {
			log.error("FordError:"+ ex.getMessage() + " with:" + ex.getClass().getSimpleName());
		}
    }
    
    protected boolean isWhiteListedException(Exception ex) {
    	if(hasCause(ex, HttpClientErrorException.class) 
    			|| hasCause(ex, HttpServerErrorException.class)
    			|| hasCause(ex, ServletRequestBindingException.class)
    			|| hasCause(ex, CircuitBreakerOpenException.class)
    			|| hasCause(ex, TypeMismatchException.class)
    			|| hasCause(ex, MultipartException.class)){
    		log.error("Whitelisted Error message: "+ex.getMessage()+" with:" + ex.getClass().getSimpleName());
    		return true;
    	}
    	return false;
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