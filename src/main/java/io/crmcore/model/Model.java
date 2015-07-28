package io.crmcore.model;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by someone on 26-Jul-2015.
 */
public interface Model {
    public static class Props {
        public static final String id = "id";
        public static final String createDate = "createDate";
        public static final String modifyDate = "modifyDate";
        public static final String createdBy = "createdBy";
        public static final String modifiedBy = "modifiedBy";
    }
}
