package com.example.demo.exceptionhandler;

import com.example.demo.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Global exception handler to handle custom exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns appropriate ResponseEntity.
     *
     * @param ex The ResourceNotFoundException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles NoContentPresentException and returns appropriate ResponseEntity.
     *
     * @param ex The NoContentPresentException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(NoContentPresentException.class)
    public ResponseEntity<Object> handleNoContentPresentException(NoContentPresentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles DateTimeException and returns appropriate ResponseEntity.
     *
     * @param ex The DateTimeException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<Object> handleInvalidDateException(DateTimeException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles JobNotPresentException and returns appropriate ResponseEntity.
     *
     * @param ex The JobNotPresentException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(JobNotPresentException.class)
    public ResponseEntity<Object> handleJobNotPresentException(JobNotPresentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles CompanyNotFoundException and returns appropriate ResponseEntity.
     *
     * @param ex The CompanyNotFoundException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<Object> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException and returns appropriate ResponseEntity.
     *
     * @param ex The IllegalArgumentException
     * @return ResponseEntity with appropriate status code and message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleCompanyNotFoundException(IllegalArgumentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        Map<String, Object> responseBody = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            String errorMessage = fieldError.getField() + " - " + fieldError.getDefaultMessage();
            errors.add(errorMessage);
        }

        responseBody.put("message", "Validation failed");
        responseBody.put("cause", errors);

        return ResponseEntity.status(status).body(responseBody);
    }
}
