package io.crm.core.service;

import io.crm.core.Events;
import io.crm.core.model.UserType;
import io.crm.core.util.ExceptionUtil;
import io.crm.core.App;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class BrService {
    public static final String id_prefix = "br-";
    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private UserService userService;

    public void create(Message<JsonObject> message) {

        final JsonObject br = message.body();

        userService.create(br, message, newUserId(), UserType.employee, r1 -> {
            if (r1.succeeded()) {
                ExceptionUtil.fail(message, r1.cause());
                return;
            }
            message.reply(null);
            App.bus.publish(Events.NEW_BR_CREATED, br);
        });
    }

    public String newUserId() {
        return String.format(id_prefix + "%04d", atomicLong.incrementAndGet());
    }
}
