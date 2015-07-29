package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Set;

/**
 * Created by someone on 09-Jun-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends Employee {
    Admin () {}
}