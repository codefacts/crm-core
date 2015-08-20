package io.crm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by someone on 19/08/2015.
 */
public class WrapMessage implements Message<JsonObject> {
    private final Message<JsonObject> msg;

    public WrapMessage(Message msg) {
        this.msg = msg;
    }

    @Override
    public String address() {
        return msg.address();
    }

    @Override
    public MultiMap headers() {
        return msg.headers();
    }

    @Override
    public JsonObject body() {
        return msg.body();
    }

    @Override
    public String replyAddress() {
        return msg.replyAddress();
    }

    @Override
    public void reply(Object message) {
        msg.reply(message);
    }

    @Override
    public <R> void reply(Object message, Handler<AsyncResult<Message<R>>> replyHandler) {
        msg.reply(message, replyHandler);
    }

    @Override
    public void reply(Object message, DeliveryOptions options) {
        msg.reply(message, options);
    }

    @Override
    public <R> void reply(Object message, DeliveryOptions options, Handler<AsyncResult<Message<R>>> replyHandler) {
        msg.reply(message, options, replyHandler);
    }

    @Override
    public void fail(int failureCode, String message) {
        msg.fail(failureCode, message);
    }
};
