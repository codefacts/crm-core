package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.Strings;
import io.crmcore.model.*;
import io.crmcore.repository.AdminRepository;
import io.crmcore.repository.HeadOfficeRepository;
import io.crmcore.repository.UserBasicRepository;
import io.crmcore.repository.UserIndexRepository;
import io.crmcore.util.ExceptionUtil;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class HeadOfficeService {
    public static final String id_prifix = "ad-";

    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private HeadOfficeRepository repository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserBasicService userBasicService;
    @Autowired
    private UserIndexService userIndexService;

    public void create(Message<JsonObject> message) {
        ExceptionUtil.withReplyRun(() -> {
            final JsonObject json = message.body();
            final HeadOffice headOffice = new HeadOffice();
            employeeService.fill(headOffice, json);

            headOffice.setCreatedBy(json.getString(Strings.createdBy, Strings.__self));
            headOffice.setModifiedBy(json.getString(Strings.modifiedBy, Strings.__self));
            Date date = new Date();
            headOffice.setCreateDate(date);
            headOffice.setModifyDate(date);

            final UserBasic basic = userBasicService.save(json.getString(Strings.username), json.getString(Strings.password));

            repository.save(headOffice);

            final UserIndex userIndex = userIndexService.save(headOffice.getId(), UserType.admin, String.format(id_prifix + "%04d", atomicLong.incrementAndGet()));

            basic.setUserIndex(userIndex);
            userBasicService.update(basic);

            App.bus.publish(Events.NEW_HEAD_OFFICE_CREATED, headOffice);

        }, message);
    }
}
