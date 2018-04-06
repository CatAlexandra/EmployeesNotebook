package com.palyaeva.validation;

/**
 * Exception that is thrown when input data or xml file is invalid.
 * Used in {@link PersonValidator}
 * */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
