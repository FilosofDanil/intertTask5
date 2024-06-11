package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when content is not present
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NoContentPresentException extends RuntimeException{
    public NoContentPresentException(String message) {
        super(message);
    }
}
