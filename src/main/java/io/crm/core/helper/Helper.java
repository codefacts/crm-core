package io.crm.core.helper;

import io.crm.QC;
import io.crm.model.EmployeeType;
import io.crm.model.User;
import io.crm.util.ErrorBuilder;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

import static io.crm.util.Util.parseMongoDate;

/**
 * Created by someone on 13-Aug-2015.
 */
public class Helper {

    public static final ThreadLocal<HashMap<Long, EmployeeType>> HASH_MAP_THREAD_LOCAL = new ThreadLocal<HashMap<Long, EmployeeType>>() {
        @Override
        protected HashMap<Long, EmployeeType> initialValue() {
            final HashMap<Long, EmployeeType> employeeTypeHashMap = new HashMap<>();
            for (EmployeeType employeeType : EmployeeType.values()) {
                employeeTypeHashMap.put(employeeType.id, employeeType);
            }
            return employeeTypeHashMap;
        }
    };

    public static Map<Long, EmployeeType> employeeTypeMap() {
        return HASH_MAP_THREAD_LOCAL.get();
    }

    public static EmployeeType employeeType(final JsonObject user) {
        return employeeTypeMap().get(user.getJsonObject(User.userType).getLong(QC.id));
    }

    public static boolean checkRequired(ErrorBuilder errorBuilder, Object val, String fieldName, String message) {
        final boolean isNull = val == null;
        if (isNull) errorBuilder.put(fieldName, message);
        return isNull;
    }

    public static boolean validateDateFormat(ErrorBuilder errorBuilder, String date, String fieldName, String errorMessage) {
        final boolean isInvalid = parseMongoDate(date, null) == null;
        if (isInvalid) errorBuilder.put(fieldName, errorMessage);
        return isInvalid;
    }
}
