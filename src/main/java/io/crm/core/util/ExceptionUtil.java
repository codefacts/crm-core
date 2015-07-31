package io.crm.core.util;

import io.crm.core.intfs.*;
import io.crm.core.intfs.Runnable;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.http.HttpStatus;

/**
 * Created by someone on 26-Jul-2015.
 */
public class ExceptionUtil {
    public static void sallowRun(io.crm.core.intfs.Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T sallowCall(Callable<T> runnable) {
        try {
            return runnable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void withReplyRun(Runnable runnable, Message message) {
        try {
            runnable.run();
            message.reply(HttpStatus.OK.value());
        } catch (Exception e) {
            message.fail(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name());
            e.printStackTrace();
        }
    }

    public static <T> T withReplyCall(Callable<T> runnable, Message message) {
        try {
            final T t = runnable.call();
            message.reply(t);
            return t;
        } catch (Exception e) {
            message.fail(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name());
            e.printStackTrace();
        }
        return null;
    }

    public static void fail(Message message, Throwable throwable) {
        message.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), throwable.getMessage());
    }

    public static void sallowCall(Callable<JsonObject> callable, AsyncResultHandler<JsonObject> asyncResultHandler) {
        try {
            JsonObject jsonObject = callable.call();
            asyncResultHandler.handle(asyncResult(jsonObject, null, true));
        } catch (Exception ex) {
            asyncResultHandler.handle(asyncResult(null, ex, false));
        }
    }

    private static AsyncResult asyncResult(JsonObject ret, Throwable e, boolean success) {
        AsyncResult<JsonObject> result = new AsyncResult<JsonObject>() {
            @Override
            public JsonObject result() {
                return ret;
            }

            @Override
            public Throwable cause() {
                return e;
            }

            @Override
            public boolean succeeded() {
                return success;
            }

            @Override
            public boolean failed() {
                return !success;
            }
        };
        return result;
    }
}
