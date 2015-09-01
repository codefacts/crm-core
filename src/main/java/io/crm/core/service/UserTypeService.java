package io.crm.core.service;

import io.crm.Events;
import io.crm.core.App;
import io.crm.mc;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;

/**
 * Created by someone on 20/08/2015.
 */
@Component
public class UserTypeService {
    final mc regions = mc.user_types;
    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public UserTypeService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnCreate(obj, a -> {

            app.getMongoClient().insert(regions + "", obj.put(id, regions.getNextId()),
                    withReply(rr -> {
                        regions.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_USER_TYPE_CREATED, obj);
                        System.out.println("USER_TYPE CREATION SUCCESSFUL. USER_TYPE: " + obj);
                    }, message));

        }, regions, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(regions + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.USER_TYPE_UPDATED, obj);
                        System.out.println("USER_TYPE SUCCESSFUL. USER_TYPE: " + obj);
                    }, message));

        }, regions, message);
    }
}
