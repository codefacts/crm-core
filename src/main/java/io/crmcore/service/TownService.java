package io.crmcore.service;

import io.crmcore.model.Town;
import io.crmcore.repository.TownRepository;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class TownService {
    @Autowired
    private TownRepository repository;

    public void findAll(Message message) {
        Iterable<Town> towns = repository.findAll();
        message.reply(towns);
    }


}
