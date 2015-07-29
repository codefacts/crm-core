package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Set;

/**
 * Created by someone on 14-Jul-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaCoordinator extends Employee {
    public static final String area = "area";
    AreaCoordinator() {}
}
