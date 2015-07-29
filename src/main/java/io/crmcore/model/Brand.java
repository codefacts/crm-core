package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand implements Model {
    public static final String name = "name";
    public static final String active = "active";
    Brand() {}
}
