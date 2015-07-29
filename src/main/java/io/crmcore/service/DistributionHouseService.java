package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.MongoCollections;
import io.crmcore.model.DistributionHouse;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class DistributionHouseService {

    public void findAll(Message message) {
        App.mongoClient.find(MongoCollections.distribution_house, new JsonObject(), r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }
            message.reply(r.result());
        });
    }
}
