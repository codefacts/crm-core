package io.crm.core.service;

import io.crm.core.App;
import io.crm.core.Events;
import io.crm.mc;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;

/**
 * Created by someone on 31/08/2015.
 */
@Component
public class CampaignService {
    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public CampaignService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnCreate(obj, a -> {
            app.getMongoClient().insert(campaigns + "", obj.put(id, campaigns.getNextId()),
                    withReply(rr -> {
                        campaigns.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_CAMPAIGN_CREATED, obj);
                        System.out.println("CAMPAIGN CREATION SUCCESSFUL. BRAND: " + obj);
                    }, message));

        }, campaigns, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(campaigns + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.CAMPAIGN_UPDATED, obj);
                        System.out.println("CAMPAIGN UPDATE SUCCESSFUL. CAMPAIGN: " + obj);
                    }, message));

        }, campaigns, message);
    }
}
