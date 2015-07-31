package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by someone on 09-Jun-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends Employee {
    Admin () {}
}