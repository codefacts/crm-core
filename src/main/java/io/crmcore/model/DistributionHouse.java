package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionHouse implements Serializable, Model {
    public static final String name = "name";
    public static final String area = "area";
    public static final String active = "active";
    DistributionHouse() {}
}
