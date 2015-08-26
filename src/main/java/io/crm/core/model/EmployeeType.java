package io.crm.core.model;

/**
 * Created by someone on 29-Jul-2015.
 */
public enum EmployeeType {
    admin(1, "ad", "Admin"),
    head_office(2, "ho", "Head Office User"),
    area_coordinator(3, "ac", "Area Coordinator"),
    br_supervisor(4, "sp", "Br Supervisor"),
    call_operator(5, "co", "Call Operator"),
    call_center_supervisor(6, "cs", "Call Center Supervisor"),
    br(7, "br", "BR");

    public final Long id;
    public final String prefix;
    public final String label;

    EmployeeType(long id, String prefix, String label) {
        this.id = id;
        this.prefix = prefix;
        this.label = label;
    }
}
