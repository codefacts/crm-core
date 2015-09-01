package io.crm.core.service;

import io.crm.Events;
import io.crm.core.App;
import io.crm.mc;
import io.crm.core.model.Query;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class AreaService {
    final mc collection = mc.areas;
    final mc parent = mc.regions;
    final String parentField = Query.region;
    final String parentLabel = parent.label;
    final String parentIdField = Query.regionId;
    final String ON_CREATE_MESSAGE = Events.NEW_AREA_CREATED;
    final String ON_UPDATE_MESSAGE = Events.AREA_UPDATED;
    final String NAME_CAPITALIZED = "AREA";

    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public AreaService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        dbService.create(message, collection, parent, parentField, parentLabel, parentIdField, ON_CREATE_MESSAGE, NAME_CAPITALIZED);
    }

    public void update(Message<JsonObject> message) {
        dbService.update(message, collection, parent, parentField, parentLabel, parentIdField, ON_UPDATE_MESSAGE, NAME_CAPITALIZED);
    }

}
