package io.crm.core.service;

import io.crm.core.util.ExceptionUtil;
import io.crm.core.App;
import io.crm.core.MC;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class DistributionHouseService {

    public void findAll(Message message) {
        App.mongoClient.find(MC.distribution_house, new JsonObject(), r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }
            message.reply(r.result());
        });
    }
}
