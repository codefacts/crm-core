package io.crm.core.util;

import com.sun.javaws.exceptions.InvalidArgumentException;
import io.crm.core.App;
import io.crm.core.model.EmployeeType;
import io.crm.core.model.Model;
import io.crm.core.model.User;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by sohan on 8/1/2015.
 */
public class Util {
    public static final String mongoDateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(mongoDateFormatString);
        }
    };

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

    public static Document toDocument(JsonObject jsonObject) {
        return toDocument(new Document(), jsonObject);
    }

    public static List toDocumentArray(JsonArray jsonObject) {
        return toDocumentArray(new ArrayList<>(), jsonObject);
    }

    public static List<Document> toDocumentList(final Collection<JsonObject> jsonObjects) {
        ArrayList<Document> documentList = new ArrayList<>();
        jsonObjects.forEach(obj -> documentList.add(toDocument(obj)));
        return documentList;
    }

    public static Document toDocument(final Document doc, final JsonObject jsonObject) {
        for (final Map.Entry<String, Object> e : jsonObject) {
            final String key = e.getKey();
            final Object value = e.getValue();
            if (value instanceof JsonObject) {
                doc.append(key, toDocument(new Document(), (JsonObject) value));
            } else if (value instanceof JsonArray) {
                doc.append(key, toDocumentArray(new ArrayList<>(), (JsonArray) value));
            }
            doc.append(key, value);
        }
        return doc;
    }

    public static List toDocumentArray(final List list, final JsonArray jsonArray) {
        for (final Object obj : jsonArray) {
            if (obj instanceof JsonObject) {
                list.add(toDocument(new Document(), (JsonObject) obj));
            } else if (obj instanceof JsonArray) {
                list.add(toDocumentArray(new ArrayList<>(), (JsonArray) obj));
            }
            list.add(obj);
        }
        return list;
    }

    public static Date toDate(final String isoString) throws ParseException {
        return mongoDateFormat().parse(isoString);
    }

    public static String toIsoString(final Date date) {
        return mongoDateFormat().format(date) + "Z";
    }

    public static void validateMongoDate(String iso_date) {
        try {
            mongoDateFormat().parse(iso_date);
        } catch (ParseException e) {
            throw new RuntimeException(new InvalidArgumentException(new String[]{"ISO DATE " + iso_date + " is invalid."}));
        }
    }

    public static JsonObject toMongoDate(String iso_string) {
        validateMongoDate(iso_string);
        return new JsonObject().put("$date", iso_string);
    }

    public static JsonObject toMongoDate(Date date) {
        return new JsonObject().put("$date", toIsoString(date));
    }

    public static Date parseMongoDate(JsonObject jsonObject) throws ParseException {
        return toDate(jsonObject.getString("$date"));
    }

    public static EmployeeType employeeType(final JsonObject user) {
        return employeeTypeMap().get(user.getJsonObject(User.userType).getLong(Model.id));
    }

    public static Map<Long, EmployeeType> employeeTypeMap() {
        return HASH_MAP_THREAD_LOCAL.get();
    }

    public static DateFormat mongoDateFormat() {
        return DATE_FORMAT_THREAD_LOCAL.get();
    }

    public static void main(String... args) throws Exception {
//        Date result1 = df1.parse(string1);

        App.testInitVertx();

        final MongoClient client = MongoClient.createShared(App.vertx, new JsonObject());
        client.insert("royl", new JsonObject().put("receive_date", toMongoDate(new Date())), r -> {
            if (r.failed()) throw new RuntimeException(r.cause());
        });

        client.findOne("royl", new JsonObject(), new JsonObject(), r -> {
            if (r.failed()) throw new RuntimeException(r.cause());
            System.out.println(r.result().getJsonObject("receive_date").getString("$date"));
        });

        System.in.read();
        App.testCloseVertx();
    }
}
