package io.crmcore.service;

import io.crmcore.model.UserIndex;
import io.crmcore.model.UserType;
import io.crmcore.repository.UserIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 26-Jul-2015.
 */
@Component
public class UserIndexService {
    @Autowired
    private UserIndexRepository repository;

    public UserIndex save(Long id, UserType userType, String userId) {
        UserIndex userIndex = new UserIndex();
        userIndex.setActualId(id);
        userIndex.setUserType(userType);
        userIndex.setUserId(userId);
        repository.save(userIndex);
        return userIndex;
    }
}
