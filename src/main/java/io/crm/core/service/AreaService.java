package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.core.App;
import io.crm.core.Events;
import io.crm.mc;
import io.crm.core.model.Query;
import io.crm.intfs.ConsumerInterface;
import io.crm.util.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;
import static io.crm.util.Util.emptyOrNull;
import static io.crm.util.Util.id;
import static io.crm.util.Util.trim;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class AreaService {
    private final App app;

    @Autowired
    public AreaService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject area = message.body();

        validateAndFormatCreate(area, a -> {

            app.getMongoClient().insert(mc.areas + "", area.put(id, mc.areas.getNextId()),
                    withReply(rr -> {
                        mc.areas.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_AREA_CREATED, area);
                        System.out.println("AREA CREATION SUCCESSFUL. AREA: " + area);
                    }, message));

        }, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject area = message.body();

        validateAndFormat(area, a -> {

            app.getMongoClient().update(mc.areas + "", new JsonObject().put(id, area.getLong(id)), Util.updateObject(area),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.AREA_UPDATED, area);
                        System.out.println("UPDATE SUCCESSFUL. AREA: " + area);
                    }, message));

        }, message);
    }

    private void validateAndFormatCreate(JsonObject area, ConsumerInterface<JsonObject> consumerInterface, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.get();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerInterface.accept(area);
                })
                .count(3)
                .message(message)
                .get();

        final String areaName = trim(area.getString(Query.name));
        final Long areaId = area.getLong(id);
        if (area == null) {
            taskCoordinator.finish();
            errorBuilder.put(Query.__self, "No data is given. Value is null.");
            return;
        }

        if (areaId != null && areaId > 0) {
            app.getMongoClient().findOne(mc.areas + "", new JsonObject().put(id, areaId), null, taskCoordinator.add(json -> {
                if (json != null) {
                    errorBuilder.put(Query.id, String.format("The ID %d already exists. Please give a valid ID.", areaId));
                    return;
                }
            }));
        } else {
            taskCoordinator.countdown();
        }

        if (emptyOrNull(areaName)) {
            taskCoordinator.countdown();
            errorBuilder.put(Query.name, "Area Name is required.");
        } else {
            app.getMongoClient().findOne(mc.areas + "", new JsonObject().put(Query.name, areaName),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json != null) {
                            errorBuilder.put(Query.name, String.format("Name %s already exists. Please give a unique name.", areaName));
                            return;
                        }

                    }));
        }

        final Long regionId = id(area, Query.region);

        if (regionId == null && regionId <= 0) {
            taskCoordinator.countdown();
            errorBuilder.put(Query.regionId, "Region ID is required.");
        } else {
            app.getMongoClient().findOne(mc.regions + "", new JsonObject().put(Query.id, regionId),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json == null) {
                            errorBuilder.put(Query.regionId, String.format("Region ID %d does not exists. Please specify a valid region.", regionId));
                            return;
                        }

                        area.put(Query.region, json);
                    }));
        }
    }

    private void validateAndFormat(JsonObject area, ConsumerInterface<JsonObject> consumerInterface, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.get();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerInterface.accept(area);
                })
                .count(2)
                .message(message)
                .get();

        final String areaName = trim(area.getString(Query.name));
        final Long areaId = area.getLong(id);
        if (area == null || areaId == null || areaId <= 0) {
            taskCoordinator.finish();
            errorBuilder.put(Query.id, "Area ID is required.");
            return;
        }

        if (emptyOrNull(areaName)) {
            taskCoordinator.countdown();
            errorBuilder.put(Query.name, "Area Name is required.");
        } else {
            app.getMongoClient().findOne(mc.areas + "", new JsonObject().put(Query.name, areaName),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json != null) {
                            final Long areaId2 = json.getLong(id);
                            if (!areaId2.equals(areaId)) {
                                errorBuilder.put(Query.name, String.format("Name %s already exists. Please give a unique name.", areaName));
                                return;
                            }
                        }

                    }));
        }

        final Long regionId = id(area, Query.region);

        if (regionId == null && regionId <= 0) {
            taskCoordinator.countdown();
            errorBuilder.put(Query.regionId, "Region ID is required.");
        } else {
            app.getMongoClient().findOne(mc.regions + "", new JsonObject().put(Query.id, regionId),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json == null) {
                            errorBuilder.put(Query.regionId, String.format("Region ID %d does not exists. Please specify a valid region.", regionId));
                            return;
                        }

                        area.put(Query.region, json);
                    }));
        }
    }

}
