package io.crmcore.service;

import io.crmcore.model.Admin;
import io.crmcore.model.Employee;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * Created by someone on 15-Jul-2015.
 */
@Component
public class EmployeeService {

    public void fill(Employee admin, JsonObject json) {
        admin.setFirstName(json.getString("firstName"));
        admin.setLastName(json.getString("lastName"));
        admin.setPhone(json.getString("phone"));
    }
}
