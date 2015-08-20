package io.crm.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
final public class App {
    private EventBus bus;
    private Vertx vertx;
    private MongoClient mongoClient;
    private JsonObject mongoConfig;
    private ConfigurableApplicationContext context;

    public static final String dateFormatString = "yyyy-MM-dd hh:mm:ss";
    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = dateFormatThreadLocal();

    private static ThreadLocal<DateFormat> dateFormatThreadLocal() {
        return new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(dateFormatString);
            }
        };
    }

    void initialize(EventBus bus, Vertx vertx, MongoClient mongoClient, JsonObject mongoConfig, ConfigurableApplicationContext context) {
        this.bus = bus;
        this.vertx = vertx;
        this.mongoClient = mongoClient;
        this.mongoConfig = mongoConfig;
        this.context = context;
    }

    public EventBus getBus() {
        return bus;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public JsonObject getMongoConfig() {
        return mongoConfig;
    }

    public ConfigurableApplicationContext getContext() {
        return context;
    }

    public static DateFormat getDefaultDateFormat() {
        return DATE_FORMAT_THREAD_LOCAL.get();
    }

    public static void main(String... args) {
        Vertx.clusteredVertx(new VertxOptions().setEventLoopPoolSize(1), new Handler<AsyncResult<Vertx>>() {

            @Override
            public void handle(AsyncResult<Vertx> e) {
                if (e.succeeded()) {
                    System.out.println("VERTEX CLUSTER STARTED");
                    e.result().deployVerticle(new MainVerticle(), new DeploymentOptions()
                            .setInstances(1));
                } else {
                    System.out.println("ERROR STARTING VERTEX CLUSTER");
                }
            }
        });
    }

    public static void testRun() {
        System.out.println("VERTEX CLUSTER STARTED");
        Vertx.vertx().deployVerticle(new MainVerticle(), new DeploymentOptions()
                .setInstances(1)
                .setMultiThreaded(false));
    }
}
