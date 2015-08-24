package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.core.App;
import io.crm.core.Events;
import io.crm.core.model.House;
import io.crm.core.model.Location;
import io.crm.core.model.Query;
import io.crm.intfs.ConsumerInterface;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
import io.crm.util.TaskCoordinator;
import io.crm.util.TaskCoordinatorBuilder;
import io.crm.util.Util;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static io.crm.core.model.Query.id;
import static io.crm.util.ExceptionUtil.withReply;

/**
 * Created by someone on 20/08/2015.
 */
@Component
public class DistributionHouseService {
    final mc collection = mc.distribution_houses;
    final mc parent = mc.areas;
    final String parentField = Query.area;
    final String parentLabel = parent.label;
    final String parentIdField = Query.areaId;
    final String ON_CREATE_MESSAGE = Events.NEW_HOUSE_CREATED;
    final String ON_UPDATE_MESSAGE = Events.HOUSE_UPDATED;
    final String NAME_CAPITALIZED = "DISTRIBUTION HOUSE";

    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public DistributionHouseService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        dbService.create(message, collection, parent, parentField, parentLabel, parentIdField, ON_CREATE_MESSAGE, NAME_CAPITALIZED);
    }

    public void update(Message<JsonObject> message) {
        dbService.update(message, collection, parent, parentField, parentLabel, parentIdField, ON_UPDATE_MESSAGE, NAME_CAPITALIZED);
    }

    public void updateLocations() {
        app.getMongoClient().find(mc.locations.name(), new JsonObject(), r -> {
            if (r.failed()) throw new RuntimeException(r.cause());

            final List<JsonObject> list = r.result();
            final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                    .count(list.size())
                    .onSuccess(() -> System.out.println("DONE"))
                    .onError(e -> e.printStackTrace()).get();

            list.forEach(j -> {
                j.remove(Location.distributionHouse);
                app.getMongoClient().update(mc.distribution_houses.name(), new JsonObject().put(Query.id, j.getLong(Query.id, 0L)),
                        Util.updateObject(new JsonObject().put(House.locations, new JsonArray().add(j))), taskCoordinator.add(rr -> {
                            System.out.println("COMPLETE: " + j.getString(Query.name) + " RESP: " + rr);
                        }));
            });
        });
    }

    public static void testUpdateLocations(String... args) throws IOException {
        MongoClient mongoClient = MongoClient.createShared(Vertx.vertx(), new JsonObject().put("db_name", "phase_0"));
        final App app = new App();
        app.initialize(Vertx.vertx().eventBus(), Vertx.vertx(), mongoClient, new JsonObject(), null);
        DistributionHouseService service = new DistributionHouseService(app);
        service.updateLocations();
        System.out.println("OK");
        System.in.read();
    }
}
