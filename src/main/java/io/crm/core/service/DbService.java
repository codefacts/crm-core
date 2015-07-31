package io.crm.core.service;

import io.crm.core.App;
import io.crm.core.MongoCollections;
import io.crm.core.model.Area;
import io.crm.core.model.Br;
import io.crm.core.model.DistributionHouse;
import io.crm.core.model.Model;
import io.crm.core.util.ExceptionUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by someone on 30-Jul-2015.
 */
@Component
public class DbService {

    public void treeWithSummary(Message<JsonObject> message) {
        App.mongoClient.find(MongoCollections.region, new JsonObject(), new Handler<AsyncResult<List<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<List<JsonObject>> event) {
                if (event.failed()) {
                    ExceptionUtil.fail(message, event.cause());
                    return;
                }
                final List<JsonObject> regions = event.result();

                regions.forEach(region -> {
                    App.mongoClient.find(MongoCollections.area, new JsonObject().put(Area.region, region.getString(Model.id)), new Handler<AsyncResult<List<JsonObject>>>() {
                        @Override
                        public void handle(AsyncResult<List<JsonObject>> event) {
                            if (event.failed()) {
                                ExceptionUtil.fail(message, event.cause());
                                return;
                            }

                            final List<JsonObject> areas = event.result();
                            region.put(MongoCollections.area, areas);

                            areas.forEach(area -> {
                                App.mongoClient.find(MongoCollections.distribution_house, new JsonObject().put(DistributionHouse.area, area.getString(Model.id)), new Handler<AsyncResult<List<JsonObject>>>() {
                                    @Override
                                    public void handle(AsyncResult<List<JsonObject>> event) {
                                        if (event.failed()) {
                                            ExceptionUtil.fail(message, event.cause());
                                            return;
                                        }

                                        final List<JsonObject> houses = event.result();
                                        area.put(MongoCollections.distribution_house, houses);

                                        houses.forEach(house -> {
                                            App.mongoClient.find(MongoCollections.br, new JsonObject().put(Br.distributionHouse, house.getString(Model.id)), new Handler<AsyncResult<List<JsonObject>>>() {
                                                @Override
                                                public void handle(AsyncResult<List<JsonObject>> event) {
                                                    if (event.failed()) {
                                                        ExceptionUtil.fail(message, event.cause());
                                                        return;
                                                    }

                                                    final List<JsonObject> brs = event.result();
                                                    house.put(MongoCollections.br, brs);
                                                }
                                            });
                                        });
                                    }
                                });
                            });
                        }
                    });
                });
            }
        });
    }
}
