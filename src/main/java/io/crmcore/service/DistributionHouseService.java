package io.crmcore.service;

import io.crmcore.model.DistributionHouse;
import io.crmcore.repository.DistributionHouseRepository;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class DistributionHouseService {
    @Autowired
    private DistributionHouseRepository repository;

    public void findAll(Message message) {
        Iterable<DistributionHouse> houses = repository.findAll();
        message.reply(houses);
    }
}
