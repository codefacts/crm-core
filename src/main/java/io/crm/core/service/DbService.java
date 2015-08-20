package io.crm.core.service;

import io.crm.core.App;
import io.crm.core.model.AllIDs;
import io.crm.core.model.Query;
import io.crm.intfs.ConsumerInterface;
import io.crm.mc;
import io.crm.util.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public void nextId(String mongoCollection, ConsumerInterface<Long> consumerInterface, Message message) {

        final Touple2<List<JsonObject>, List<JsonObject>> touple2 = new Touple2<>();

        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create().count(2).message(message)
                .onSuccess(() -> {

                    final List<JsonObject> list1 = touple2.t1;
                    final List<JsonObject> list2 = touple2.t2;

                    final Long max1 = list1.size() <= 0 ? 0L : list1.get(0).getLong(Query.id);
                    final Long max2 = list2.size() <= 0 ? 0L : list2.get(0).getLong(AllIDs.assigned_id);

                    Long nextId = (max1 > max2 ? max1 : max2) + 1L;
                    app.getMongoClient().insert(mc.all_ids.name(), new JsonObject()
                                    .put(AllIDs.key, mongoCollection).put(AllIDs.assigned_id, nextId),
                            ExceptionUtil.withReply(jj -> {
                                consumerInterface.accept(nextId);
                            }, message));
                    return;
                }).get();

        app.getMongoClient().findWithOptions(mongoCollection, new JsonObject(), new FindOptions()
                .setFields(new JsonObject().put(Query.id, 1))
                .setSort(new JsonObject().put(Query.id, -1))
                .setLimit(1), taskCoordinator.add(list -> {
            touple2.t1 = list;
        }));

        app.getMongoClient().findWithOptions(mc.all_ids.name(), new JsonObject().put(AllIDs.key, mongoCollection), new FindOptions()
                .setLimit(1).setSort(new JsonObject().put(AllIDs.assigned_id, -1))
                .setFields(new JsonObject().put(AllIDs.assigned_id, 1).put(Query.id, 0)), taskCoordinator.add(list -> {
            touple2.t2 = list;
        }));
    }
}
