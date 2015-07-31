package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Area {
    public static final String name = "name";
    public static final String region = "region";
    public static final String active = "active";
    Area() {}
}
