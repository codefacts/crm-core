package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.core.App;
import io.crm.core.Events;
import io.crm.core.model.Query;
import io.crm.intfs.ConsumerInterface;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
import io.crm.util.TaskCoordinator;
import io.crm.util.TaskCoordinatorBuilder;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;
import static io.crm.util.Util.emptyOrNull;
import static io.crm.util.Util.trim;

@Component
public class RegionService {
    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public RegionService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnCreate(obj, a -> {
            final mc regions = mc.regions;
            app.getMongoClient().insert(regions + "", obj.put(id, regions.getNextId()),
                    withReply(rr -> {
                        regions.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_REGION_CREATED, obj);
                        System.out.println("REGION CREATION SUCCESSFUL. AREA: " + obj);
                    }, message));

        }, mc.regions, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(mc.regions + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.REGION_UPDATED, obj);
                        System.out.println("UPDATE SUCCESSFUL. REGION: " + obj);
                    }, message));

        }, mc.regions, message);
    }
}
