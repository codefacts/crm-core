package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.QC;
import io.crm.core.App;
import io.crm.intfs.*;
import io.crm.mc;
import io.crm.util.*;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.crm.QC.id;
import static io.crm.util.ExceptionUtil.withReply;
import static io.crm.util.ExceptionUtil.withReplyRun;
import static io.crm.util.Util.*;

/**
 * Created by someone on 19/08/2015.
 */
@Component
public class DbService {
    private final App app;

    @Autowired
    public DbService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message,
                       final mc collection,
                       final mc parent,
                       final String parentField,
                       final String parentLabel,
                       final String parentIdField,
                       final String ON_CREATE_MESSAGE,
                       final String NAME_CAPITALIZED) {

        final DbService dbService = this;
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnCreate(obj, a -> {

            dbService.validateParentId(a, aa -> {

                app.getMongoClient().insert(collection + "", obj.put(id, collection.getNextId()),
                        withReply(rr -> {
                            collection.incrementNextId();
                            message.reply(null);
                            app.getBus().publish(ON_CREATE_MESSAGE, obj);
                            System.out.println(NAME_CAPITALIZED + " CREATION SUCCESSFUL. " + NAME_CAPITALIZED + ": " + obj);
                        }, message));
            }, parent, parentField, parentIdField, parentLabel, message);

        }, collection, message);
    }

    public void update(final Message<JsonObject> message,
                       final mc collection,
                       final mc parent,
                       final String parentField,
                       final String parentLabel,
                       final String parentIdField,
                       final String ON_UPDATE_MESSAGE,
                       final String NAME_CAPITALIZED) {

        final DbService dbService = this;
        final JsonObject obj = message.body();

        dbService.validateIdAndNameOnEdit(obj, a -> {

            dbService.validateParentId(obj, aa -> {
                app.getMongoClient().update(collection + "", new JsonObject().put(id, obj.getLong(id)), Util.updateObject(obj),
                        withReply(rr -> {
                            message.reply(null);
                            app.getBus().publish(ON_UPDATE_MESSAGE, obj);
                            System.out.println(String.format("UPDATE SUCCESSFUL. %s: " + obj, NAME_CAPITALIZED));
                        }, message));
            }, parent, parentField, parentIdField, parentLabel, message);

        }, collection, message);
    }

    public void validateIdAndNameOnCreate(JsonObject obj, ConsumerUnchecked<JsonObject> consumerUnchecked, final mc collection, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.build();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerUnchecked.accept(obj);
                })
                .count(2)
                .message(message)
                .get();

        final String objName = trim(obj.getString(QC.name));
        final Long objId = obj.getLong(id);
        if (obj == null) {
            errorBuilder.put(QC.__self, "No data is given. Value is null.");
            taskCoordinator.finish();
            return;
        }

        if (objId != null && objId > 0) {
            app.getMongoClient().findOne(collection + "", new JsonObject().put(id, objId), null, taskCoordinator.add(json -> {
                if (json != null) {
                    errorBuilder.put(QC.id, String.format("The ID %d already exists. Please give a valid ID.", objId));
                    return;
                }
            }));
        } else {
            taskCoordinator.countdown();
        }

        if (isEmptyOrNullOrSpaces(objName)) {
            errorBuilder.put(QC.name, String.format("%s Name is required.", collection.label));
            taskCoordinator.countdown();
        } else {
            app.getMongoClient().findOne(collection + "", new JsonObject().put(QC.name, objName),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json != null) {
                            errorBuilder.put(QC.name, String.format("Name %s already exists. Please give a unique name.", objName));
                            return;
                        }

                    }));
        }
    }

    public void validateIdAndNameOnEdit(JsonObject obj, ConsumerUnchecked<JsonObject> consumerUnchecked, final mc collection, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.build();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerUnchecked.accept(obj);
                })
                .count(1)
                .message(message)
                .get();

        final String objName = trim(obj.getString(QC.name));
        final Long objId = obj.getLong(id);
        if (obj == null || objId <= 0) {
            errorBuilder.put(QC.id, String.format("%d ID is required.", collection.label));
            taskCoordinator.finish();
            return;
        }

        if (isEmptyOrNullOrSpaces(objName)) {
            errorBuilder.put(QC.name, String.format("%s Name is required.", collection));
            taskCoordinator.countdown();
        } else {
            app.getMongoClient().findOne(collection + "", new JsonObject().put(QC.name, objName),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json != null) {
                            final Long objId2 = json.getLong(id);
                            if (!objId2.equals(objId)) {
                                errorBuilder.put(QC.name, String.format("Name %s already exists. Please give a unique name.", objName));
                                return;
                            }
                        }

                    }));
        }
    }

    public void validateParentId(JsonObject obj, ConsumerUnchecked<JsonObject> consumerUnchecked, mc parent, String parentField, String parentIdField, String parentLabel, Message message) {

        final ErrorBuilder errorBuilder = ErrorBuilder.create();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .onSuccess(() -> {
                    final JsonObject errors = errorBuilder.build();
                    if (errors.size() > 0) {
                        message.fail(FailureCode.validationError.code, errors.encode());
                        return;
                    }
                    consumerUnchecked.accept(obj);
                })
                .count(1)
                .message(message)
                .get();

        final Long parentId = Util.id(obj.getValue(parentField));

        if (parentId == null || parentId <= 0) {
            errorBuilder.put(parentIdField, parentLabel + " ID is required.");
            taskCoordinator.countdown();
        } else {
            app.getMongoClient().findOne(parent + "", new JsonObject().put(QC.id, parentId),
                    new JsonObject(), taskCoordinator.add(json -> {

                        if (json == null) {
                            errorBuilder.put(parentIdField, String.format("%s ID %d does not exists. Please specify a valid %s.", parentLabel, parentId, parentField));
                            return;
                        }

                        obj.put(parentField, json);
                    }));
        }
    }

    public void validateBrandId(final Long brandId, final ConsumerUnchecked<JsonObject> consumer, final Message message) {
        app.getMongoClient().findOne(mc.brands.name(), new JsonObject().put(QC.id, brandId),
                new JsonObject().put(QC.id, true), rr -> {

                    if (rr.failed()) {
                        ExceptionUtil.fail(message, rr.cause());
                        return;
                    }

                    withReplyRun(() -> consumer.accept(rr.result() == null ? null :
                            new JsonObject()
                                    .put(QC.message, "Brand ID is invalid.")
                                    .put(QC.required, true)), message);
                });
    }

    public static void main(String... args) throws Exception {
        ErrorBuilder errorBuilder = new ErrorBuilder();

        final MongoClient mongoClient = MongoClient.createShared(Vertx.vertx(), new JsonObject().put("db_name", "phase_0"));
        mongoClient.findOne(mc.brands.name(), new JsonObject().put(QC.id, 2),
                new JsonObject().put(QC.id, true), rr -> {

                    if (rr.failed()) {
                        throw new RuntimeException(rr.cause());
                    }
                    System.out.println("result: >>> " + rr.result());
                });

        System.in.read();
    }
}
