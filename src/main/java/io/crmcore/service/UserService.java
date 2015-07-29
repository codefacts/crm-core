package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.MongoCollections;
import io.crmcore.exceptions.ValidationException;
import io.crmcore.model.Model;
import io.crmcore.model.User;
import io.crmcore.model.UserType;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by someone on 29-Jul-2015.
 */
@Component
public class UserService {
    @Autowired
    private UserIndexService userIndexService;

    public void create(JsonObject user, Message message, String newUserId, UserType userType, AsyncResultHandler<String> handler) {
        Date date = new Date();
        user.put(Model.createDate, date);
        user.put(Model.modifyDate, date);

        validate(user, new AsyncResultHandler<JsonObject>() {
            @Override
            public void handle(AsyncResult<JsonObject> event) {
                userIndexService.create(user.getString(User.username), result -> {
                    if (result.failed()) {
                        ExceptionUtil.fail(message, result.cause());
                        return;
                    }
                    final String index_id = result.result();
                    App.mongoClient.insert(mongo_collection(userType), user, r -> {
                        if (r.failed()) {
                            ExceptionUtil.fail(message, r.cause());
                            return;
                        }
                        final String admin_id = r.result();

                        UserIndexService.update(index_id, newUserId, admin_id, userType, handler);
                    });
                });
            }
        });
    }

    private String mongo_collection(UserType userType) {
        switch (userType) {
            case employee:
                return MongoCollections.employee;
            case client:
                return MongoCollections.client;
            case consumer:
                return MongoCollections.consumer;
        }
        return null;
    }

    private void validate(JsonObject user, AsyncResultHandler<JsonObject> asyncResultHandler) {
        ExceptionUtil.sallowCall(() -> {
            final String usrType = user.getString(User.userType);
            if (usrType == null || usrType.trim().isEmpty()) {
                new ValidationException("UserType is required.");
            }
            return user;
        }, asyncResultHandler);
    }
}
