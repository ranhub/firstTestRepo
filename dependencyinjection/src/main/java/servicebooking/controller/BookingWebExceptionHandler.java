package servicebooking.controller;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.ford.turbo.aposb.common.basemodels.input.StatusContext;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse.RequestStatus;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class BookingWebExceptionHandler {

	@ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
	@Order(1)
    public BaseResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BaseResponse response = new BaseResponse();
		String errorMessages = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.sorted()
				.collect(Collectors.joining(", "));
		
		response.setLastRequested(ZonedDateTime.now());
		response.setRequestStatus(RequestStatus.UNAVAILABLE);
		response.setError(new FordError(StatusContext.HTTP.getStatusContext(), BAD_REQUEST.value(), errorMessages));
		return response;
    }
	
	@ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(JsonMappingException.class)
	@Order(1)
    public BaseResponse jsonMappingException(JsonMappingException ex) {
		BaseResponse response = new BaseResponse();
		String fieldsErrorMessage = ex.getOriginalMessage();
		
		response.setLastRequested(ZonedDateTime.now());
		response.setRequestStatus(RequestStatus.UNAVAILABLE);
		response.setError(new FordError(StatusContext.HTTP.getStatusContext(), BAD_REQUEST.value(), fieldsErrorMessage));
		return response;
    }
}
