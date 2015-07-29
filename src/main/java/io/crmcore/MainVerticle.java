package io.crmcore;

import io.crmcore.codec.*;
import io.crmcore.model.*;
import io.crmcore.service.*;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by someone on 08-Jul-2015.
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("--------------Strating verticle");
        App.vertx = getVertx();
        App.bus = getVertx().eventBus();

        getVertx().executeBlocking((Future<ConfigurableApplicationContext> e) -> {
            App.mongoClient = MongoClient.createShared(getVertx(), new JsonObject(loadConfig()));
            final ConfigurableApplicationContext context = SpringApplication.run(App.class);
            e.complete(context);

        }, r -> {
            if (r.succeeded()) {
                final ConfigurableApplicationContext context = r.result();
                registerCodecs(context);
                registerEvents(context);
                startFuture.complete();
                System.out.println("Spring App Complete..");
            } else {
                startFuture.fail(r.cause());
                System.out.println("Spring App Error..");
            }
        });

        getVertx().eventBus().consumer("test.message", objectMessage -> System.out.println("--------------GOT MSG: " + objectMessage.body()));

        System.out.println("--------------Verticle complete");
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
}
