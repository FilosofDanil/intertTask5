package com.example.demo.exceptions;

/**
 * Exception thrown when resource is not available
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
