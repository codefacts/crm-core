package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.model.*;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class AdminService {
    public static final String id_prifix = "ad-";
    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private UserService userService;

    public void create(Message<JsonObject> message) {

        final JsonObject admin = message.body();

        userService.create(admin, message, newUserId(), UserType.employee, r1 -> {
            if (r1.succeeded()) {
                ExceptionUtil.fail(message, r1.cause());
                return;
            }
            message.reply(null);
            App.bus.publish(Events.NEW_ADMIN_CREATED, admin);
        });

    }

    public String newUserId() {
        return String.format(id_prifix + "%04d", atomicLong.incrementAndGet());
    }
}
