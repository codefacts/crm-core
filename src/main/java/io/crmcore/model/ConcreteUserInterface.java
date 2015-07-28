package io.crmcore.model;

import java.util.Date;
import java.util.Set;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface ConcreteUserInterface<T> extends UserInterface<T> {

    public Set<Role> getRoles();

    public void setRoles(Set<Role> roles);

    public boolean isActive();

    public void setActive(boolean active);

    public Date getJoinDate();

    public void setJoinDate(Date joinDate);

    public Date getResignDate();

    public void setResignDate(Date resignDate);

    public Date getCreateDate();

    public void setCreateDate(Date createDate);

    public Date getModifyDate();

    public void setModifyDate(Date modifyDate);

    public String getCreatedBy();

    public void setCreatedBy(String createdBy);

    public String getModifiedBy();

    public void setModifiedBy(String modifiedBy);
}
