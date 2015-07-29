package io.crmcore;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;

@SpringBootApplication
public class App {
    public static EventBus bus;
    public static Vertx vertx;
    public static MongoClient mongoClient;

    @Autowired
    Environment env;

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat(env.getProperty("spring.jackson.date-format", "yyyy-MMM-dd hh:mm:ss a")))
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false);
        return builder;
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }

    @Bean
    MappingJackson2HttpMessageConverter jackson2HttpMessageConverter(Jackson2ObjectMapperBuilder jacksonBuilder) {
        ObjectMapper objectMapper = jacksonBuilder.build();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        return converter;
    }

    public static void main(String... args) {
        Vertx.clusteredVertx(new VertxOptions(new JsonObject()), new Handler<AsyncResult<Vertx>>() {

            @Override
            public void handle(AsyncResult<Vertx> e) {
                if (e.succeeded()) {
                    System.out.println("VERTEX CLUSTER STARTED");
                    e.result().deployVerticle(new MainVerticle());
                } else {
                    System.out.println("ERROR STARTING VERTEX CLUSTER");
                }
            }
        });
    }
}
