package io.crm.core;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import io.crm.IndexTouple;
import io.crm.Indexes;
import io.crm.core.model.EmployeeType;
import io.crm.core.model.Query;
import io.crm.core.service.*;
import io.crm.mc;
import io.crm.util.AsyncUtil;
import io.crm.util.TaskCoordinator;
import io.crm.util.TaskCoordinatorBuilder;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static io.crm.core.model.Query.*;
import static io.crm.util.AsyncUtil.fail;
import static io.crm.util.AsyncUtil.success;

/**
 * Created by someone on 08-Jul-2015.
 */
public class MainVerticle extends AbstractVerticle {
    private Future<Void> startFuture;
    private int count = mc.values().length;
    private App app;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("--------------Strating verticle");
        this.startFuture = startFuture;

        final JsonObject config = new JsonObject(loadConfig());
        final MongoClient mongoClient = MongoClient.createShared(getVertx(), config);
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("io.crm.core", "io.crm.core.service", "io.crm.core.codec");
        context.start();

        app = context.getBean(App.class);

        app.initialize(getVertx().eventBus(), getVertx(), mongoClient, config, context);

        createAndInitDb(r -> {
            if (r.failed()) {
                onFail(r.cause());
                return;
            }

            onDbInialized();
        });

        System.out.println("--------------Verticle complete");
    }

    private void onFail(Throwable throwable) {
        if (startFuture != null && !startFuture.isComplete()) {
            startFuture.fail(throwable);
            startFuture = null;
            System.out.println("<------------------------FAILED----------------------->");
        }
    }

    private void createAndInitDb(AsyncResultHandler<Void> handler) {

        createCollections(r -> {
            if (r.failed()) {
                handler.handle(fail(r.cause()));
                return;
            }

            createIndexes(rr -> {
                if (rr.failed()) {
                    handler.handle(fail(rr.cause()));
                    return;
                }
                createIds(r2 -> {
                    if (r2.failed()) {
                        handler.handle(fail(rr.cause()));
                        return;
                    }
                    createUserTypes(handler);
                });
            });
        });
    }

    private void createIds(AsyncResultHandler<Void> handler) {

        final TaskCoordinator taskCoordinator = new TaskCoordinatorBuilder().create().count(mc.values().length)
                .onSuccess(() -> handler.handle(success()))
                .onError(e -> handler.handle(fail(e))).get();

        for (mc m : mc.values()) {
            app.getMongoClient().findWithOptions(m.name(), new JsonObject(), new FindOptions()
                    .setLimit(1)
                    .setSort(new JsonObject()
                            .put(id, -1))
                    .setFields(new JsonObject().put(id, 1)), taskCoordinator.add(list -> {

                if (m.isIdTypeLong()) {
                    m.setNextId(list.size() <= 0 ? 1 : (list.get(0).getLong(id) + 1));
                    final long nextId = m.getNextId();
                    final int length = EmployeeType.values().length;
                    if (m == mc.user_types) {
                        m.setNextId(nextId <= length ? length : nextId);
                    }
                    System.out.println(String.format("SETTING NEXT ID %d FOR %s", nextId, m.name()));
                }
            }));
        }
    }

    private void createUserTypes(final AsyncResultHandler<Void> handler) {
        final TaskCoordinator taskCoordinator = new TaskCoordinatorBuilder().create().count(EmployeeType.values().length)
                .onSuccess(() -> handler.handle(success()))
                .onError(e -> handler.handle(fail(e))).get();

        for (EmployeeType employeeType : EmployeeType.values()) {
            app.getMongoClient().insert(mc.user_types.name(), new JsonObject()
                    .put(id, employeeType.id)
                    .put(name, employeeType.name())
                    .put(prefix, employeeType.prefix)
                    .put(label, employeeType.label), r -> {
                if (r.failed()) {
                    if (r.cause() instanceof MongoWriteException) {
                        MongoWriteException exception = (MongoWriteException) r.cause();
                        System.out.println("MongoWriteException CODE: " + exception.getCode());
                        if (exception.getCode() == 11000) {
                            taskCoordinator.countdown();
                            return;
                        }
                    }
                    taskCoordinator.signalError(r.cause());
                    return;
                }
                System.out.println(String.format("INDEX CREATION SUCCESSFUL. INDEX [ID: %d, NAME: %s]. RESPONSE: ", employeeType.id, employeeType.name()) + r);
            });
        }
    }

    private void createCollections(AsyncResultHandler<Void> handler) {
        final MongoClient mongoClient = app.getMongoClient();

        mongoClient.runCommand("listCollections", new JsonObject().put("listCollections", ""), r -> {
            if (r.failed()) {
                handler.handle(fail(r.cause()));
                return;
            }
            System.out.println(r.result());

            final JsonArray array = r.result().getJsonObject("cursor").getJsonArray("firstBatch");

            Map<String, JsonObject> map = new HashMap<String, JsonObject>();
            for (int i = 0; i < array.size(); i++) {
                map.put(array.getJsonObject(i).getString("name"), null);
            }

            for (mc m : mc.values()) {
                createCollection(mc.regions + "", handler, map);
            }
        });
    }

    private void createIndexes(AsyncResultHandler<Void> handler) {
        final TaskCoordinator taskCoordinator = TaskCoordinatorBuilder.create()
                .count(Indexes.values().length)
                .onError(e -> handler.handle(fail(e)))
                .onSuccess(() -> handler.handle(success(null)))
                .get();

        app.getMongoClient().find(system_indexes, new JsonObject(), r -> {
            if (r.failed()) {
                handler.handle(fail(r.cause()));
                return;
            }

            final Set<String> indexes = r.result().stream().map(json -> json.getString(name)).collect(Collectors.toSet());

            for (Indexes index : Indexes.values()) {
                if (indexes.contains(index.name())) {
                    taskCoordinator.countdown();
                    continue;
                }
                app.getMongoClient().runCommand(mm.createIndexes,
                        new JsonObject()
                                .put(mm.createIndexes, index.collection)
                                .put(Query.indexes, new JsonArray()
                                        .add(new JsonObject()
                                                .put(key, keys(index.kyes))
                                                .put(name, index.name())
                                                .put(unique, index.unique))), taskCoordinator.add(cr -> System.out.println("Index created successfully. Index: " + index)));
            }

        });
    }

    private JsonObject keys(IndexTouple[] kyes) {
        final JsonObject entries = new JsonObject();
        for (IndexTouple k : kyes) {
            entries.put(k.key, k.value);
        }
        return entries;
    }

    private void createCollection(String collectionName, AsyncResultHandler<Void> handler, Map<String, JsonObject> map) {

        if (map.containsKey(collectionName)) {
            count--;
            if (count <= 0) {
                handler.handle(success(null));
            }
            return;
        }

        app.getMongoClient().runCommand("create", new JsonObject().put("create", collectionName), r -> {
            if (r.failed()) {
                handler.handle(fail(r.cause()));
                return;
            }
            count--;
            if (count <= 0) {
                handler.handle(success(null));
            }
        });
    }

    private void onDbInialized() {
        onSpringContextLoaded(app.getContext());
    }

    private void onSpringContextLoaded(final ConfigurableApplicationContext context) {
        registerCodecs(context);
        registerEvents(context);
        onComplete();
    }

    private void registerCodecs(ConfigurableApplicationContext ctx) {

    }

    private void registerEvents(ConfigurableApplicationContext ctx) {
        final EventBus bus = getVertx().eventBus();

        bus.consumer(Events.UPDATE_AREA, ctx.getBean(AreaService.class)::update);
        bus.consumer(Events.CREATE_AREA, ctx.getBean(AreaService.class)::create);
        bus.consumer(Events.UPDATE_REGION, ctx.getBean(RegionService.class)::update);
        bus.consumer(Events.CREATE_REGION, ctx.getBean(RegionService.class)::create);
        bus.consumer(Events.UPDATE_HOUSE, ctx.getBean(DistributionHouseService.class)::update);
        bus.consumer(Events.CREATE_HOUSE, ctx.getBean(DistributionHouseService.class)::create);
        bus.consumer(Events.UPDATE_LOCATION, ctx.getBean(LocationService.class)::update);
        bus.consumer(Events.CREATE_LOCATION, ctx.getBean(LocationService.class)::create);
        bus.consumer(Events.UPDATE_BRAND, ctx.getBean(BrandService.class)::update);
        bus.consumer(Events.CREATE_BRAND, ctx.getBean(BrandService.class)::create);
        bus.consumer(Events.UPDATE_USER_TYPE, ctx.getBean(UserTypeService.class)::update);
        bus.consumer(Events.CREATE_USER_TYPE, ctx.getBean(UserTypeService.class)::create);
        bus.consumer(Events.CREATE_CAMPAIGN, ctx.getBean(CampaignService.class)::create);
        bus.consumer(Events.UPDATE_CAMPAIGN, ctx.getBean(CampaignService.class)::update);
    }

    public static String loadConfig() {
        InputStream stream = MainVerticle.class.getResourceAsStream("/mongo-config.json");
        try {
            String string = IOUtils.toString(stream, "UTF-8");
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private void onComplete() {
        startFuture.complete();
        startFuture = null;
        System.out.println("<-------------------COMPLETE-------------------->");
    }

    @Override
    public void stop() throws Exception {
        app.getContext().close();
        app.getMongoClient().close();
    }
}
