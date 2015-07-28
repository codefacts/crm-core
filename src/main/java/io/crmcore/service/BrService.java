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
public class BrService {
    public static final String id_prifix = "br-";
    private final AtomicLong atomicLong = new AtomicLong(0L);
    @Autowired
    private BrRepository repository;
    @Autowired
    private UserBasicRepository userBasicRepository;
    @Autowired
    private UserIndexRepository userIndexRepository;
    @Autowired
    private DistributionHouseRepository distributionHouseRepository;
    @Autowired
    private TownRepository townRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private EmployeeService employeeService;

    public void create(Message<JsonObject> message) {
        ExceptionUtil.withReplyRun(() -> {
            final JsonObject json = message.body();
            final Br br = new Br();
            employeeService.fill(br, json);

            br.setCreatedBy(json.getString(Strings.createdBy, Strings.__self));
            br.setModifiedBy(json.getString(Strings.modifiedBy, Strings.__self));
            Date date = new Date();
            br.setCreateDate(date);
            br.setModifyDate(date);

            //Br specific
            br.setDistributionHouse(distributionHouseRepository.findOne(json.getLong("distributionHouse")));
            br.setTown(townRepository.findOne(json.getLong("town")));
            br.setBrand(brandRepository.findOne(json.getLong("brand")));

            //UserInterface basic
            UserBasic basic = new UserBasic();
            basic.setUsername(json.getString(Strings.username));
            basic.setPassword(json.getString(Strings.password));
            userBasicRepository.save(basic);

            repository.save(br);

            UserIndex userIndex = new UserIndex();
            userIndex.setActualId(br.getId());
            userIndex.setUserType(UserType.admin);
            userIndex.setUserId(String.format(id_prifix + "%04d", atomicLong.incrementAndGet()));
            userIndexRepository.save(userIndex);

            basic.setUserIndex(userIndex);
            userBasicRepository.save(basic);

            App.bus.publish(Events.NEW_BR_CREATED, br);

        }, message);
    }
}
