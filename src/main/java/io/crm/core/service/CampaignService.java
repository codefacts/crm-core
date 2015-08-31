package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.core.App;
import io.crm.core.Events;
import io.crm.core.model.Campaign;
import io.crm.core.model.Query;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;
import static io.crm.util.Util.id;
import static io.crm.util.Util.updateObject;

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
        final JsonObject campaign = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnCreate(campaign, obj -> {

            app.getMongoClient().findOne(mc.brands.name(), new JsonObject().put(Query.id, Util.id(campaign.getValue(Campaign.brand))), new JsonObject(), brandResutl -> {

                if (brandResutl.failed()) {
                    final ErrorBuilder errorBuilder = ErrorBuilder.create();
                    errorBuilder.put(Campaign.brand, "Brand ID is invalid.");
                    final JsonObject errors = errorBuilder.get();
                    message.fail(FailureCode.validationError.code, errors.encode());
                    return;
                }

                app.getMongoClient().insert(campaigns + "", campaign.put(id, campaigns.getNextId())
                                .put(Campaign.brand, brandResutl.result()),
                        withReply(rr -> {
                            campaigns.incrementNextId();
                            message.reply(null);
                            app.getBus().publish(Events.NEW_CAMPAIGN_CREATED, campaign);
                            System.out.println("CAMPAIGN CREATION SUCCESSFUL. CAMPAIGN: " + campaign);
                        }, message));

            });

        }, campaigns, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(campaigns + "", new JsonObject().put(id, obj.getLong(id)), updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.CAMPAIGN_UPDATED, obj);
                        System.out.println("CAMPAIGN UPDATE SUCCESSFUL. CAMPAIGN: " + obj);
                    }, message));

        }, campaigns, message);
    }
}
