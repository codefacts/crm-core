package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by someone on 14-Jul-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sup extends Employee {
    public static final String distributionHouse = "distributionHouse";
    public static final String campaign = "campaign";
    public static final String ac = "ac";
    Sup() {}
}
