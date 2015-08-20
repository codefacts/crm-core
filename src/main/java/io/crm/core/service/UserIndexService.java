package io.crm.core.service;

import io.crm.core.model.*;
import io.crm.core.App;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserIndexService {
    private final App app;
    public static final String mongo_collection = "user_indices";

    @Autowired
    public UserIndexService(App app) {
        this.app = app;
    }

    public void create(String username, AsyncResultHandler<String> handler) {
        app.getMongoClient().insert(mongo_collection, new JsonObject().put(User.username, username), handler);
    }

    public static void update(App app, String index_id, String newUserId, String admin_id, UserType userType, AsyncResultHandler<String> asyncResultHandler) {
        JsonObject index = new JsonObject().put(Query.id, index_id)
                .put(UserIndex.userType, userType)
                .put(UserIndex.userId, newUserId)
                .put(UserIndex.actualId, admin_id);

        app.getMongoClient().save(mongo_collection, index, asyncResultHandler);
    }
}
