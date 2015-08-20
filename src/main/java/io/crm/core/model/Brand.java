package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand {
    public static final String name = "name";
    public static final String active = "active";
    public static final String shortName = "shortName";

    Brand() {
    }
}
