package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a job is not present in enum.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class JobNotPresentException extends RuntimeException{
    public JobNotPresentException(String message) {
        super(message);
    }
}
