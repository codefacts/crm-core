package io.crm.core.service;

import io.crm.core.App;
import io.crm.core.MC;
import io.crm.core.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class TownService {

    public void findAll(Message message) {
        App.mongoClient.find(MC.location, new JsonObject(), r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }
            message.reply(r.result());
        });
    }


}
