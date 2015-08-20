package io.crm;

/**
 * Created by someone on 18/08/2015.
 */
public enum FailureCode {
    validationError(1, "Validation Error"), InternalServerError(2, "Internal Server Error");

    public final int code;
    public final String message;

    FailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
