package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.Strings;
import io.crmcore.model.Admin;
import io.crmcore.model.UserBasic;
import io.crmcore.model.UserIndex;
import io.crmcore.model.UserType;
import io.crmcore.repository.AdminRepository;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class AdminService {
    public static final String id_prifix = "ad-";
    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private AdminRepository repository;
    @Autowired
    private UserBasicService userBasicService;
    @Autowired
    private UserIndexService userIndexService;
    @Autowired
    private EmployeeService employeeService;

    public void create(Message<JsonObject> message) {

        ExceptionUtil.withReplyRun(() -> {
            final JsonObject json = message.body();
            final Admin admin = new Admin();
            employeeService.fill(admin, json);

            admin.setCreatedBy(json.getString(Strings.createdBy, Strings.__self));
            admin.setModifiedBy(json.getString(Strings.modifiedBy, Strings.__self));
            Date date = new Date();
            admin.setCreateDate(date);
            admin.setModifyDate(date);

            UserBasic basic = userBasicService.save(json.getString(Strings.username), json.getString(Strings.password));

            repository.save(admin);

            final UserIndex userIndex = userIndexService.save(admin.getId(), UserType.admin, String.format(id_prifix + "%04d", atomicLong.incrementAndGet()));

            basic.setUserIndex(userIndex);
            userBasicService.update(basic);

            App.bus.publish(Events.NEW_ADMIN_CREATED, admin);

        }, message);
    }
}
