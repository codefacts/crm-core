package io.crm.core.util;

import io.vertx.core.AsyncResult;

/**
 * Created by someone on 30-Jul-2015.
 */
public class AsyncUtil {

    public static <T> AsyncResult<T> success() {
        return new AsyncResult<T>() {
            @Override
            public T result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return true;
            }

            @Override
            public boolean failed() {
                return false;
            }
        };
    }

    public static <T> AsyncResult<T> success(final T value) {
        return new AsyncResult<T>() {
            @Override
            public T result() {
                return value;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return true;
            }

            @Override
            public boolean failed() {
                return false;
            }
        };
    }

    public static <T> AsyncResult<T> fail(final Throwable throwable) {
        return new AsyncResult<T>() {
            @Override
            public T result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return throwable;
            }

            @Override
            public boolean succeeded() {
                return false;
            }

            @Override
            public boolean failed() {
                return true;
            }
        };
    }
}
