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

        validateAndFormatCreate(obj, a -> {
            final mc regions = mc.regions;
            app.getMongoClient().insert(regions + "", obj.put(id, regions.getNextId()),
                    withReply(rr -> {
                        regions.incrementNextId();
                        message.reply(null);
                        app.getBus().publish(Events.NEW_REGION_CREATED, obj);
                        System.out.println("REGION CREATION SUCCESSFUL. AREA: " + obj);
                    }, message));

        }, message);
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();

        validateAndFormat(obj, a -> {

            app.getMongoClient().update(mc.regions + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.REGION_UPDATED, obj);
                        System.out.println("UPDATE SUCCESSFUL. REGION: " + obj);
                    }, message));

        }, message);
    }

    private void validateAndFormatCreate(JsonObject obj, ConsumerInterface<JsonObject> consumerInterface, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.get();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerInterface.accept(obj);
                })
                .count(3)
                .message(message)
                .get();

        final String objName = trim(obj.getString(Query.name));
        final Long objId = obj.getLong(id);
        if (obj == null) {
            taskCoordinator.finish();
            errorBuilder.put(Query.__self, "No data is given. Value is null.");
            return;
        }

        if (objId != null && objId > 0) {
            app.getMongoClient().findOne(mc.regions + "", new JsonObject().put(id, objId), null, taskCoordinator.add(json -> {
                if (json != null) {
                    errorBuilder.put(Query.id, String.format("The ID %d already exists. Please give a valid ID.", objId));
                    return;
                }
            }));
        } else {
            taskCoordinator.countdown();
        }

        if (emptyOrNull(objName)) {
            taskCoordinator.countdown();
            errorBuilder.put(Query.name, "Region Name is required.");
        } else {
            app.getMongoClient().findOne(mc.regions + "", new JsonObject().put(Query.name, objName),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json != null) {
                            errorBuilder.put(Query.name, String.format("Name %s already exists. Please give a unique name.", objName));
                            return;
                        }

                    }));
        }

        final Long regionId = Util.id(obj, Query.region);

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

                        obj.put(Query.region, json);
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

        final Long regionId = Util.id(area, Query.region);

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
