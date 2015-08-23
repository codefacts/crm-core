package io.crm.core.service;

import io.crm.FailureCode;
import io.crm.core.App;
import io.crm.core.Events;
import io.crm.core.model.Query;
import io.crm.intfs.ConsumerInterface;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
import io.crm.util.TaskCoordinator;
import io.crm.util.TaskCoordinatorBuilder;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
