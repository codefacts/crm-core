package io.crmcore.service;

import io.crmcore.App;
import io.crmcore.Events;
import io.crmcore.Strings;
import io.crmcore.model.*;
import io.crmcore.repository.*;
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
public class AreaCoordinatorService {
    public static final String id_prefix = "ac-";

    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private AreaCoordinatorRepository repository;
    @Autowired
    private UserBasicService userBasicService;
    @Autowired
    private UserIndexService userIndexService;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private EmployeeService employeeService;

    public void create(Message<JsonObject> message) {
        ExceptionUtil.withReplyRun(() -> {
            final JsonObject json = message.body();
            final AreaCoordinator areaCoordinator = new AreaCoordinator();
            employeeService.fill(areaCoordinator, json);

            areaCoordinator.setCreatedBy(json.getString(Strings.createdBy, Strings.__self));
            areaCoordinator.setModifiedBy(json.getString(Strings.modifiedBy, Strings.__self));
            Date date = new Date();
            areaCoordinator.setCreateDate(date);
            areaCoordinator.setModifyDate(date);

            //Br specific
            areaCoordinator.setArea(areaRepository.findOne(json.getLong("area")));

            //UserInterface basic
            final UserBasic basic = userBasicService.save(json.getString(Strings.username), json.getString(Strings.password));

            repository.save(areaCoordinator);

            final UserIndex userIndex = userIndexService.save(areaCoordinator.getId(), UserType.area_coordinator, String.format(id_prefix + "%04d", atomicLong.incrementAndGet()));

            basic.setUserIndex(userIndex);
            userBasicService.update(basic);

            App.bus.publish(Events.NEW_AREA_COORDINATOR_CREATED, areaCoordinator);

        }, message);
    }
}
