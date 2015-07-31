package io.crm.core.service;

import io.crm.core.model.Model;
import io.crm.core.model.UserIndex;
import io.crm.core.model.UserType;
import io.crm.core.App;
import io.crm.core.model.User;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserIndexService {
    public static final String mongo_collection = "user_indices";

    public void create(String username, AsyncResultHandler<String> handler) {
        App.mongoClient.insert(mongo_collection, new JsonObject().put(User.username, username), handler);
    }

    public static void update(String index_id, String newUserId, String admin_id, UserType userType, AsyncResultHandler<String> asyncResultHandler) {
        JsonObject index = new JsonObject().put(Model.id, index_id)
                .put(UserIndex.userType, userType)
                .put(UserIndex.userId, newUserId)
                .put(UserIndex.actualId, admin_id);

        App.mongoClient.save(mongo_collection, index, asyncResultHandler);
    }
}
