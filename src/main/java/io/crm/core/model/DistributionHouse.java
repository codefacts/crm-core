package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionHouse implements Serializable, Model {
    public static final String name = "name";
    public static final String area = "area";
    public static final String active = "active";
    DistributionHouse() {}
}
