package io.crmcore.util;

import io.crmcore.intfs.*;
import io.vertx.core.eventbus.Message;
import org.springframework.http.HttpStatus;

/**
 * Created by someone on 26-Jul-2015.
 */
public class ExceptionUtil {
    public static void sallowRun(io.crmcore.intfs.Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T sallowCall(io.crmcore.intfs.Callable<T> runnable) {
        try {
            return runnable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void withReplyRun(io.crmcore.intfs.Runnable runnable, Message message) {
        try {
            runnable.run();
            message.reply(HttpStatus.OK.value());
        } catch (Exception e) {
            message.fail(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name());
            e.printStackTrace();
        }
    }

    public static <T> T withReplyCall(io.crmcore.intfs.Callable<T> runnable, Message message) {
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
}
