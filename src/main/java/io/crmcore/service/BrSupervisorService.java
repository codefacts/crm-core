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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class BrSupervisorService {
    public static final String id_prefix = "sup-";

    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private BrSupervisorRepository repository;
    @Autowired
    private UserBasicRepository userBasicRepository;
    @Autowired
    private UserIndexRepository userIndexRepository;
    @Autowired
    private DistributionHouseRepository distributionHouseRepository;
    @Autowired
    private EmployeeService employeeService;

    public void create(Message<JsonObject> message) {
        ExceptionUtil.withReplyRun(() -> {
            final JsonObject json = message.body();
            final BrSupervisor brSupervisor = new BrSupervisor();
            employeeService.fill(brSupervisor, json);

            brSupervisor.setCreatedBy(json.getString(Strings.createdBy, Strings.__self));
            brSupervisor.setModifiedBy(json.getString(Strings.modifiedBy, Strings.__self));
            Date date = new Date();
            brSupervisor.setCreateDate(date);
            brSupervisor.setModifyDate(date);

            //Br specific
            brSupervisor.setDistributionHouse(distributionHouseRepository.findOne(json.getLong("distributionHouse")));

            //User basic
            UserBasic basic = new UserBasic();
            basic.setUsername(json.getString(Strings.username));
            basic.setPassword(json.getString(Strings.password));
            userBasicRepository.save(basic);

            repository.save(brSupervisor);

            UserIndex userIndex = new UserIndex();
            userIndex.setActualId(brSupervisor.getId());
            userIndex.setUserType(UserType.admin);
            userIndex.setUserId(String.format(id_prefix + "%04d", atomicLong.incrementAndGet()));
            userIndexRepository.save(userIndex);

            basic.setUserIndex(userIndex);
            userBasicRepository.save(basic);

            App.bus.publish(Events.NEW_BR_SUPERVISOR_CREATED, brSupervisor);
        }, message);
    }
}
