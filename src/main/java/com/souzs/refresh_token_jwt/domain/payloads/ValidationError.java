package com.souzs.refresh_token_jwt.domain.payloads;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {
    private final List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String name, String message) {
        errors.removeIf(error -> error.getName().equals(name));

        errors.add(new FieldMessage(name, message));
    }
}
