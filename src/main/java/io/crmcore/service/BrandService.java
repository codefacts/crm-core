package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.MongoCollections;
import io.crmcore.model.Brand;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class BrandService {

    public void findAll(Message message) {
        App.mongoClient.find(MongoCollections.brand, new JsonObject(), r -> {
            if (r.failed()) {
                ExceptionUtil.fail(message, r.cause());
                return;
            }

            message.reply(r.result());
        });
    }
}
