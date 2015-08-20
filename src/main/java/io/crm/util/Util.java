package io.crm.util;

import io.crm.core.model.Query;
import io.crm.util.exceptions.InvalidArgumentException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.crm.core.model.Query.$set;

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
            throw new InvalidArgumentException("ISO DATE " + iso_date + " is invalid.");
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

    public static DateFormat mongoDateFormat() {
        return DATE_FORMAT_THREAD_LOCAL.get();
    }

    public static Long id(JsonObject area, String field) {
        final Object value = area.getValue(field);
        if (value instanceof JsonObject) {
            JsonObject json = (JsonObject) value;
            return json.getLong(Query.id);
        }
        return area.getLong(field);
    }

    public static void main(String... args) {
        System.out.println(new JsonObject().getValue("kkk"));
    }

    public static JsonObject updateObject(JsonObject area) {
        area.remove(Query.id);
        return new JsonObject().put($set, area);
    }

    public static String trim(String string) {
        return string == null ? "" : string.trim();
    }

    public static boolean emptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

}
