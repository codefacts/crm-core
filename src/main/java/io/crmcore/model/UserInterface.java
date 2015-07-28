package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.*;

public interface UserInterface<T> extends Identifiable<T> {

    public void setId(Long id);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public Date getDateOfBirth();

    public void setDateOfBirth(Date dateOfBirth);

    public String getMail();

    public void setMail(String mail);

    public String getPhone();

    public void setPhone(String phone);

    public Address getAddress();

    public void setAddress(Address address);
}
