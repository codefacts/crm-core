package io.crmcore.service;

import io.crmcore.Strings;
import io.crmcore.model.UserBasic;
import io.crmcore.repository.UserBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserBasicService {
    @Autowired
    private UserBasicRepository repository;

    public UserBasic save(String username, String password) {
        UserBasic basic = new UserBasic();
        basic.setUsername(username);
        basic.setPassword(password);
        repository.save(basic);
        return basic;
    }

    public void update(UserBasic basic) {
        repository.save(basic);
    }
}
