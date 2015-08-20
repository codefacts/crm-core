package io.crm.core.service;

import io.crm.core.App;
import io.crm.mc;
import io.crm.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class TownService {
    private final App app;

    @Autowired
    public TownService(App app) {
        this.app = app;
    }

    public void findAll(Message message) {
        app.getMongoClient().find(mc.locations + "", new JsonObject(), r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }
            message.reply(r.result());
        });
    }


}
