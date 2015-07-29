package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.model.Model;
import io.crmcore.model.User;
import io.crmcore.model.UserIndex;
import io.crmcore.model.UserType;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
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
