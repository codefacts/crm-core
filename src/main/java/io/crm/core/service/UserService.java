package io.crm.core.service;

import io.crm.QC;
import io.crm.model.UserType;
import io.crm.core.App;
import io.crm.mc;
import io.crm.core.exceptions.ValidationException;
import io.crm.model.User;
import io.crm.util.ExceptionUtil;
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
    private final App app;
    private final UserIndexService userIndexService;

    @Autowired
    public UserService(App app, UserIndexService userIndexService) {
        this.app = app;
        this.userIndexService = userIndexService;
    }


    public void create(JsonObject user, Message message, String newUserId, UserType userType, AsyncResultHandler<String> handler) {
        Date date = new Date();
        user.put(QC.createDate, date);
        user.put(QC.modifyDate, date);

        validate(user, new AsyncResultHandler<JsonObject>() {
            @Override
            public void handle(AsyncResult<JsonObject> event) {
                userIndexService.create(user.getString(User.username), result -> {
                    if (result.failed()) {
                        ExceptionUtil.fail(message, result.cause());
                        return;
                    }
                    final String index_id = result.result();
                    app.getMongoClient().insert(mongo_collection(userType), user, r -> {
                        if (r.failed()) {
                            ExceptionUtil.fail(message, r.cause());
                            return;
                        }
                        final String admin_id = r.result();

                        UserIndexService.update(app, index_id, newUserId, admin_id, userType, handler);
                    });
                });
            }
        });
    }

    private String mongo_collection(UserType userType) {
        switch (userType) {
            case employee:
                return mc.employees + "";
            case client:
                return mc.clients + "";
            case consumer:
                return mc.consumers + "";
        }
        return null;
    }

    private void validate(JsonObject user, AsyncResultHandler<JsonObject> asyncResultHandler) {
        ExceptionUtil.then(() -> {
            final String usrType = user.getString(User.userType);
            if (usrType == null || usrType.trim().isEmpty()) {
                new ValidationException("UserType is required.");
            }
            return user;
        }, asyncResultHandler);
    }
}
