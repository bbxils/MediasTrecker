package ru.itis.exceptions;

import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;   // поле -> текст ошибки

    public ValidationException(Map<String, String> errors) {
        super("Ошибки в форме: " + errors);
        this.errors = errors;
    }
    public Map<String, String> getErrors() { return errors; }
}
