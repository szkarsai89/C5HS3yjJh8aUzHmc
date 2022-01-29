package hu.wup.bt.challenge.clientenroll.config;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import hu.wup.bt.challenge.restapi.swagger.model.BaseResponse;
import hu.wup.bt.challenge.restapi.swagger.model.Message;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		BaseResponse response = new BaseResponse();
		
		ex.getAllErrors().stream().forEach(error -> {
			Message errorMessage = new Message();
			if (error instanceof FieldError)
			{
				errorMessage.setFieldId(((FieldError) error).getField());
			}
			errorMessage.setCode(error.getCode());
			errorMessage.setMessage(error.getDefaultMessage());
			response.addErrorItem(errorMessage);
		});
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		if (ex instanceof HttpMessageNotReadableException)
		{
			try {
				HttpMessageNotReadableException hmnre = (HttpMessageNotReadableException) ex;
				InvalidFormatException ife = (InvalidFormatException)hmnre.getCause();
				String fieldName = ife.getPath().get(0).getFieldName();
				BaseResponse response = new BaseResponse();
				response.addErrorItem(new Message().code("InvalidFormat").fieldId(fieldName).message("Value format is invalid!"));
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} catch (Exception notExpectedException)
			{
				return super.handleExceptionInternal(ex, body, headers, status, request);
			}
		}
		else
		{
			return super.handleExceptionInternal(ex, body, headers, status, request);
		}
	}
	
}
