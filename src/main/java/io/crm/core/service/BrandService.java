package io.crm.core.service;

import io.crm.Events;
import io.crm.core.App;
import io.crm.mc;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.QC.id;
import static io.crm.util.ExceptionUtil.withReply;

/**
 * Created by someone on 20/08/2015.
 */
@Component
public class BrandService {
    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public BrandService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc brands = mc.brands;
        dbService.validateIdAndNameOnCreate(obj, a -> {
            app.getMongoClient().insert(brands + "", obj.put(id, brands.getNextId()),
                    withReply(rr -> {
                        brands.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_BRAND_CREATED, obj);
                        System.out.println("BRAND CREATION SUCCESSFUL. BRAND: " + obj);
                    }, message));

        }, brands, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc brands = mc.brands;
        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(brands + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.BRAND_UPDATED, obj);
                        System.out.println("BRAND SUCCESSFUL. BRAND: " + obj);
                    }, message));

        }, brands, message);
    }
}
