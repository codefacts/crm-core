package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by someone on 14-Jul-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaCoordinator extends Employee {
    public static final String area = "area";
    AreaCoordinator() {}
}
