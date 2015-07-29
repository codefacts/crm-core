package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.Strings;
import io.crmcore.model.*;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class HeadOfficeService {
    public static final String id_prefix = "hd-";

    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private UserService userService;

    public void create(Message<JsonObject> message) {
        final JsonObject head = message.body();
        userService.create(head, message, newUserId(), UserType.employee, r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }
            message.reply(null);
            App.bus.publish(Events.NEW_HEAD_OFFICE_CREATED, head);
        });
    }

    public String newUserId() {
        return String.format(id_prefix + "%04d", atomicLong.incrementAndGet());
    }
}
