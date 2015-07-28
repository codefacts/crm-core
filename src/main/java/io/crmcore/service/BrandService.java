package io.crmcore.service;

import io.crmcore.model.Brand;
import io.crmcore.repository.BrandRepository;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class BrandService {
    @Autowired
    private BrandRepository repository;

    public void findAll(Message message) {
        Iterable<Brand> brands = repository.findAll();
        message.reply(brands);
    }
}
