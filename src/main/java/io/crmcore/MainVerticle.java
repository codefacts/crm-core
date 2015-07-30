package io.crmcore;

import io.crmcore.codec.*;
import io.crmcore.service.*;
import io.crmcore.util.AsyncUtil;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by someone on 08-Jul-2015.
 */
public class MainVerticle extends AbstractVerticle {
    private Future<Void> startFuture;
    private int count = App.collection_count;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("--------------Strating verticle");
        this.startFuture = startFuture;
        App.vertx = getVertx();
        App.bus = getVertx().eventBus();

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
        final JsonObject config = new JsonObject(loadConfig());
        App.mongoClient = MongoClient.createShared(getVertx(), config);
        App.mongoConfig = config;

        createCollections(handler);
    }

    private void createCollections(AsyncResultHandler<Void> handler) {
        final MongoClient mongoClient = App.mongoClient;

        mongoClient.runCommand("listCollections", new JsonObject().put("listCollections", ""), r -> {
            if (r.failed()) {
                handler.handle(AsyncUtil.fail(r.cause()));
                return;
            }
            System.out.println(r.result());

            final JsonArray array = r.result().getJsonObject("cursor").getJsonArray("firstBatch");

            Map<String, JsonObject> map = new HashMap<String, JsonObject>();
            for (int i = 0; i < array.size(); i++) {
                map.put(array.getJsonObject(i).getString("name"), null);
            }

            createCollection(MongoCollections.address, handler, map);
            createCollection(MongoCollections.admin, handler, map);
            createCollection(MongoCollections.area, handler, map);
            createCollection(MongoCollections.area_coordinator, handler, map);

            createCollection(MongoCollections.br, handler, map);
            createCollection(MongoCollections.brand, handler, map);

            createCollection(MongoCollections.client, handler, map);
            createCollection(MongoCollections.consumer, handler, map);
            createCollection(MongoCollections.consumer_contact, handler, map);
            createCollection(MongoCollections.distribution_house, handler, map);

            createCollection(MongoCollections.head_office, handler, map);
            createCollection(MongoCollections.employee, handler, map);

            createCollection(MongoCollections.region, handler, map);
            createCollection(MongoCollections.town, handler, map);
            createCollection(MongoCollections.user_index, handler, map);
        });
    }

    private void createCollection(String collectionName, AsyncResultHandler<Void> handler, Map<String, JsonObject> map) {

        if (map.containsKey(collectionName)) {
            count--;
            if (count <= 0) {
                handler.handle(AsyncUtil.success(null));
            }
            return;
        }

        App.mongoClient.runCommand("create", new JsonObject().put("create", collectionName), r -> {
            if (r.failed()) {
                handler.handle(AsyncUtil.fail(r.cause()));
                return;
            }
            count--;
            if (count <= 0) {
                handler.handle(AsyncUtil.success(null));
            }
        });
    }

    private void onDbInialized() {
        getVertx().executeBlocking((Future<ConfigurableApplicationContext> future) -> {
            final ConfigurableApplicationContext context = SpringApplication.run(App.class);
            future.complete(context);
        }, r -> {
            if (r.failed()) {
                onFail(r.cause());
                return;
            }
            onSpringContextLoaded(r.result());
        });
    }

    private void onSpringContextLoaded(final ConfigurableApplicationContext context) {
        registerCodecs(context);
        registerEvents(context);
        onComplete();
    }

    private void registerCodecs(ConfigurableApplicationContext ctx) {
        App.bus.registerDefaultCodec(ArrayList.class, ctx.getBean(ArrayListToJsonArrayCodec.class));
    }

    private void registerEvents(ConfigurableApplicationContext ctx) {
        final EventBus bus = getVertx().eventBus();
        bus.consumer(Events.CREATE_NEW_ADMIN, ctx.getBean(AdminService.class)::create);
        bus.consumer(Events.CREATE_NEW_HEAD_OFFICE, ctx.getBean(HeadOfficeService.class)::create);
        bus.consumer(Events.CREATE_NEW_BR, ctx.getBean(BrService.class)::create);
        bus.consumer(Events.CREATE_NEW_BR_SUPERVISOR, ctx.getBean(BrSupervisorService.class)::create);
        bus.consumer(Events.CREATE_NEW_AREA_COORDINATOR, ctx.getBean(AreaCoordinatorService.class)::create);

        bus.consumer(Events.FIND_ALL_TOWNS, ctx.getBean(TownService.class)::findAll);
        bus.consumer(Events.FIND_ALL_DISTRIBUTION_HOUSES, ctx.getBean(DistributionHouseService.class)::findAll);
        bus.consumer(Events.FIND_ALL_BRANDS, ctx.getBean(BrandService.class)::findAll);
    }

    private String loadConfig() {
        InputStream stream = this.getClass().getResourceAsStream("/mongo-config.json");
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
        if (App.mongoClient != null) {
            App.mongoClient.close();
            App.mongoClient = null;
        }
    }
}
