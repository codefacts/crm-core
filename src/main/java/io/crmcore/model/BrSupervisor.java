package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Set;

/**
 * Created by someone on 14-Jul-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrSupervisor extends Employee implements Model {
    public static final String distributionHouse = "distributionHouse";
    BrSupervisor() {}
}
