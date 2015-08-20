package io.crm.core.exceptions;

import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 7/29/2015.
 */
public class ValidationException extends RuntimeException {
    private final JsonArray errors;

    public ValidationException(final JsonArray errors) {
        this.errors = errors;
    }

    public ValidationException() {
        errors = new JsonArray();
    }

    public ValidationException(String message) {
        super(message);
        errors = new JsonArray();
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        errors = new JsonArray();
    }

    public ValidationException(Throwable cause) {
        super(cause);
        errors = new JsonArray();
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        errors = new JsonArray();
    }
}
