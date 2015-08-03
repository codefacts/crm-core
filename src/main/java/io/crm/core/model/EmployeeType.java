package io.crm.core.model;

/**
 * Created by someone on 29-Jul-2015.
 */
public enum EmployeeType {
    admin(1, "Admin"),
    head_office(2, "Head Office User"),
    area_coordinator(3, "Area Coordinator"),
    br_supervisor(4, "Br Supervisor"),
    call_operator(5, "Call Operator"),
    call_center_supervisor(6, "Call Center Supervisor"),
    br(7, "BR");

    public final Long id;
    public final String label;

    EmployeeType(long id, String label) {
        this.id = id;
        this.label = label;
    }
}
