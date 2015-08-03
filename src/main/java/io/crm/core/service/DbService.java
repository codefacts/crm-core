package io.crm.core.service;

import com.mongodb.client.MongoCollection;
import io.crm.core.App;
import io.crm.core.model.*;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static io.crm.core.model.Model.id;
import static io.crm.core.model.Region.*;
import static io.crm.core.model.Region.name;
import static io.crm.core.util.ExceptionUtil.*;
import static io.crm.core.util.Util.toDocumentList;

/**
 * Created by sohan on 8/1/2015.
 */
public class DbService {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

    public void createDb() {
        String path = "D:\\IdeaProjects\\crm-core\\src\\main\\resources\\crm\\db.json";

//        App.vertx.executeBlocking((Future<JsonObject> f) -> {
        JsonObject dbJson = sallowCall(() -> new JsonObject(loadFile(path)));
        onLoadData(dbJson);
//            f.complete(dbJson);
//        }, r -> {
//            if (r.failed()) {
//                System.out.println("<< Db Import Failed >>");
//                return;
//            }
//        });
    }

    private void onLoadData(final JsonObject dbJson) {
        final JsonObject campaign = new JsonObject();
        final Holder holder = new Holder(dbJson, campaign);
        System.out.println("Db parsed.");
//            insert(MC.region, holder.regionList.values());
//            insert(MC.area, holder.areaList.values());
//            insert(MC.distribution_house, holder.houseList.values());
    }

    private void insert(String collectionName, Collection<JsonObject> values) {
        MongoCollection<Document> collection = App.db.getCollection(collectionName);
        collection.insertMany(toDocumentList(values));
    }

    private class Holder {
        final Map<Long, JsonObject> brandList;
        final Map<Long, JsonObject> regionList;
        final Map<Long, JsonObject> areaList;
        final Map<Long, JsonObject> houseList;
        final Map<Long, JsonObject> brCategories;
        final Map<Long, JsonObject> brList;
        final Map<Long, JsonObject> userList;
        final Map<Long, JsonObject> locationList;
        final Map<String, JsonObject> locationMap = new HashMap<>();
        final Map<Long, Set<String>> locationMapByHouse = new HashMap<>();
        final Map<Long, JsonObject> contactList;

        Holder(final JsonObject dbJson, final JsonObject campaign) {
            final UserIdGenerator userIdGenerator = new UserIdGenerator();
            brandList = brandList(dbJson);
            brCategories = brCategories(dbJson);
            regionList = regionList(dbJson);
            areaList = areaList(dbJson, regionList);
            locationList = locationList(dbJson, locationMap, locationMapByHouse);
            houseList = houseList(dbJson, areaList, locationMap, locationMapByHouse);
            userList = userList(dbJson, campaign, userIdGenerator);
            brList = brList(dbJson, houseList, locationMap, brCategories, userList, userIdGenerator);
            contactList = contactList(dbJson, campaign, brList, houseList, areaList, regionList, locationList);
        }
    }

    private Map<Long, JsonObject> brCategories(JsonObject dbJson) {
        JsonArray jsonArray = dbJson.getJsonArray("BR_CATEGORIES");
        Map<Long, JsonObject> hashMap = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            final Long categorId = json.getLong("BR_CATEGORY_ID");
            final String category = json.getString("BR_CATEGORY_NAME");
            if (!hashMap.containsKey(category)) {
                hashMap.put(categorId, new JsonObject()
                        .put(id, categorId)
                        .put(name, category));
            }
        }

        return hashMap;
    }

    private Map<Long, JsonObject> locationList(JsonObject dbJson, Map<String, JsonObject> hashMap, Map<Long, Set<String>> locationMapByHouse) {
        final JsonArray jsonArray = dbJson.getJsonArray("BRS");
        long location_id = 1;
        for (Object jsonObj : jsonArray) {
            final JsonObject json = (JsonObject) jsonObj;
            final String location = json.getString("LOCATION");
            if (!hashMap.containsKey(location)) {
                final JsonObject object = new JsonObject()
                        .put(id, location_id++)
                        .put(name, location);
                hashMap.put(location, object);
                final Long distribution_house_id = json.getLong("DISTRIBUTION_HOUSE_ID");
                if (!locationMapByHouse.containsKey(distribution_house_id)) {
                    locationMapByHouse.put(distribution_house_id, new HashSet<>());
                }
                locationMapByHouse.get(distribution_house_id).add(location);
            }
        }

        HashMap<Long, JsonObject> map = new HashMap<>();

        for (JsonObject obj : hashMap.values()) {
            map.put(obj.getLong(id), obj);
        }

        return map;
    }

    private Map<Long, JsonObject> brandList(JsonObject dbJson) {
        JsonArray regions = dbJson.getJsonArray("BRANDS");
        Map<Long, JsonObject> regionList = new HashMap<>();
        for (Object regionObj : regions) {
            JsonObject region = (JsonObject) regionObj;
            JsonObject brandEntity = new JsonObject().put(id, region.getLong("BRAND_ID"))
                    .put(name, region.getString("BRAND_NAME"))
                    .put(Brand.shortName, region.getString("BRAND_SHORT_NAME"))
                    .put(active, true);
            regionList.put(brandEntity.getLong(id), brandEntity);
        }
        return regionList;
    }

    private Map<Long, JsonObject> houseList(JsonObject dbJson, Map<Long, JsonObject> areaList, Map<String, JsonObject> locationMap, Map<Long, Set<String>> locationMapByHouse) {
        JsonArray jsonArray = dbJson.getJsonArray("DISTRIBUTION_HOUSES");
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            final Long distribution_house_id = json.getLong("DISTRIBUTION_HOUSE_ID");
            final Long area_id = json.getLong("AREA_ID");
            JsonObject entity = new JsonObject().put(id, distribution_house_id)
                    .put(name, json.getString("DISTRIBUTION_HOUSE_NAME"))
                    .put(DistributionHouse.area, areaList.get(area_id).copy())
                    .put(DistributionHouse.locations, findLocations(locationMap, locationMapByHouse, distribution_house_id))
                    .put(active, true);
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private JsonArray findLocations(Map<String, JsonObject> locationMap, Map<Long, Set<String>> locationMapByHouse, Long distribution_house_id) {
        return new JsonArray(locationMapByHouse
                .getOrDefault(distribution_house_id, new HashSet<>())
                .stream().map(ln -> newLocation(locationMap, ln)).collect(Collectors.toList()));
    }

    private Map<Long, JsonObject> areaList(JsonObject dbJson, Map<Long, JsonObject> regionList) {
        JsonArray jsonArray = dbJson.getJsonArray("AREAS");
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            Long region_id = json.getLong("REGION_ID");
            JsonObject entity = new JsonObject().put(id, json.getLong("AREA_ID"))
                    .put(name, json.getString("AREA_NAME"))
                    .put(Area.region, regionList.get(region_id).copy())
                    .put(active, true);
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private Map<Long, JsonObject> regionList(JsonObject dbJson) {
        JsonArray regions = dbJson.getJsonArray("REGIONS");
        Map<Long, JsonObject> regionList = new HashMap<>();
        for (Object regionObj : regions) {
            JsonObject region = (JsonObject) regionObj;
            JsonObject regionEntity = new JsonObject().put(id, region.getLong("REGION_ID"))
                    .put(name, region.getString("REGION_NAME"))
                    .put(active, true);
            regionList.put(regionEntity.getLong(id), regionEntity);
        }
        return regionList;
    }

    private Map<Long, JsonObject> userList(JsonObject dbJson, JsonObject campaign, UserIdGenerator userIdGenerator) {
        JsonArray jsonArray = dbJson.getJsonArray("USERS");
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            final Long user_type_id = json.getLong("USER_TYPE");
            final EmployeeType employeeType = userType(user_type_id);
            JsonObject user = new JsonObject()
                    .put(id, json.getLong("USER_ID"))
                    .put(User.userId, userIdGenerator.nextId(user_type_id))
                    .put(User.userType, userTypeObj(employeeType))
                    .put(User.username, json.getString("USER_NAME"))
                    .put(User.name, json.getString("EMPLOY_NAME"))
                    .put(User.mobile, json.getString("MOBILE"))
                    .put(User.password, json.getString("password"))
                    .put(active, json.getInteger("ACTIVE"));

            if (employeeType == EmployeeType.br_supervisor) {
                user.put(BrSupervisor.campaign, campaign.copy());
            }

            list.put(user.getLong(id), user);
        }
        return list;
    }

    private Map<Long, JsonObject> brList(JsonObject dbJson, Map<Long, JsonObject> houseList, Map<String, JsonObject> locationMap, Map<Long, JsonObject> brCategories, Map<Long, JsonObject> userList, UserIdGenerator userIdGenerator) {
        JsonArray jsonArray = dbJson.getJsonArray("BRS");
        Map<Long, JsonObject> list = new HashMap<>();

        for (Object jsonObj : jsonArray) {

            final JsonObject json = (JsonObject) jsonObj;
            final JsonObject house = newHouse(houseList, json.getLong("DISTRIBUTION_HOUSE_ID"));
            final JsonObject category = newCategory(brCategories, json.getLong("BR_CATEGORY_ID"));
            final Long supId = json.getLong("BR_SUP_ID");
            final JsonObject supervisor = newSupervisor(userList, supId);
            JsonObject entity = new JsonObject().put(id, json.getLong("BR_ID"))
                    .put(name, json.getString("BR_NAME"))
                    .put(User.userId, userIdGenerator.nextId(EmployeeType.br.id))
                    .put(User.userType, userTypeObj(EmployeeType.admin.br))
                    .put(Br.distributionHouse, house)
                    .put(Br.mobile, json.getString("BR_MOBILE_1"))
                    .put(Br.supervisor, supervisor)
                    .put(Br.category, category)
                    .put(Br.location, newLocation(locationMap, json.getString("LOCATION")))
                    .put(Br.joinDate, sallowCall(() -> json.getString("JOIN_DATE")))
                    .put(Br.resignDate, sallowCall(() -> json.getString("RESIGN_DATE")))
                    .put(Br.remarks, json.getString("REMARKS"))
                    .put(active, json.getInteger("ACTIVE"));
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private JsonObject newSupervisor(Map<Long, JsonObject> userList, Long supId) {
        final JsonObject object = userList.get(supId);
        return object != null ? object.copy() : null;
    }

    private Map<Long, JsonObject> contactList(JsonObject dbJson, JsonObject campaign, Map<Long, JsonObject> brList, Map<Long, JsonObject> houseList, Map<Long, JsonObject> areaList, Map<Long, JsonObject> regionList, Map<Long, JsonObject> locationList) {
        final JsonArray jsonArray = dbJson.getJsonArray("SMS_INBOX");
        final Map<Long, JsonObject> list = new HashMap<>();
        for (final Object jsonObj : jsonArray) {
            final JsonObject json = (JsonObject) jsonObj;

            final JsonObject br = brList.get(json.getLong("BR_ID")).copy();
            final JsonObject house = newHouse(houseList, br.getJsonObject(Br.distributionHouse).getLong(id));
            final JsonObject area = newArea(areaList, house);
            final JsonObject region = newRegion(regionList, area);

            br.put(Br.distributionHouse, house);
            house.put(DistributionHouse.area, area);
            area.put(Area.region, region);
            //Brand
            JsonObject entity = new JsonObject()
                    .put(id, json.getLong("SMS_ID"))

                    .put(ConsumerContact.br, br)
                    .put(ConsumerContact.campaign, campaign)
                    .put(ConsumerContact.consumer, consumer(json))

                    .put(ConsumerContact.brand, json.getString("Brand"))
                    .put(ConsumerContact.ptr, json.getBoolean("PTR"))
                    .put(ConsumerContact.swp, json.getBoolean("SWAP"))
                    .put(ConsumerContact.date, json.getString("Date"))
                    .put(ConsumerContact.receive_date, json.getString("RECEIVE_DATE"));
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private JsonObject consumer(JsonObject json) {
        return new JsonObject()
                .put(ConsumerContact.name, json.getString("Customer_Name"))
                .put(ConsumerContact.mobile, json.getString("CONSUMER_MOBILE"))
                .put(ConsumerContact.fatherName, json.getString("FATHER_NAME"))
                .put(ConsumerContact.age, json.getInteger("Age"))
                .put(ConsumerContact.occupation, json.getString("Occupation"));
    }

    private JsonObject userTypeObj(EmployeeType employeeType) {
        return new JsonObject()
                .put(id, employeeType.id)
                .put(name, employeeType.name())
                .put(User.Type.label, employeeType.label);
    }

    private EmployeeType userType(Long user_type_id) {
        return Arrays.asList(EmployeeType.values()).stream().filter(t -> t.id == user_type_id).findAny().get();
    }

    private JsonObject newCategory(Map<Long, JsonObject> brCategories, Long br_category_id) {
        return brCategories.get(br_category_id).copy();
    }

    private JsonObject newLocation(Map<String, JsonObject> locationList, String location) {
        return locationList.get(location).copy();
    }

    private JsonObject newHouse(Map<Long, JsonObject> houseList, long houseId) {
        return houseList.get(houseId).copy();
    }

    private JsonObject newArea(Map<Long, JsonObject> areaList, JsonObject house) {
        return areaList
                .get(house
                                .getJsonObject(DistributionHouse.area)
                                .getLong(id)
                ).copy();
    }

    private JsonObject newRegion(Map<Long, JsonObject> regionList, JsonObject area) {
        return regionList.get(area.getJsonObject(Area.region).getLong(id)).copy();
    }

    private String loadFile(String path) throws IOException {
        return IOUtils.toString(new FileInputStream(path));
    }

    private static final class UserIdGenerator {
        final UserId ad = new UserId("ad-");
        final UserId ho = new UserId("ho-");
        final UserId ac = new UserId("ac-");
        final UserId sp = new UserId("sp-");
        final UserId br = new UserId("br-");
        final UserId co = new UserId("co-");
        final UserId cs = new UserId("cs-");

        public String nextId(Long user_type_id) {
            switch (user_type_id.intValue()) {
                case 1:
                    return ad.nextId();
                case 2:
                    return ho.nextId();
                case 3:
                    return ac.nextId();
                case 4:
                    return sp.nextId();
                case 5:
                    return co.nextId();
                case 6:
                    return cs.nextId();
                case 7:
                    return br.nextId();
                default:
                    throw new RuntimeException("UserId could not be generated. UserTypeId = " + user_type_id);
            }
        }
    }

    private static final class UserId {
        final static Set<String> prefixes = new HashSet<>();
        final String prefix;
        long id = 1;

        private UserId(String prefix) {
            if (!prefixes.add(prefix)) {
                throw new RuntimeException("Invalid user id prefix. This prefix '" + prefix + "' already assigned. Assigned Prifixes are : " + prefixes);
            }
            this.prefix = prefix;
        }

        public String nextId() {
            return String.format(prefix + "%05d", (id++));
        }
    }

    public static void main(String... args) throws IOException {
        App.testInitVertx();

        System.out.println("<<<<<<<<<<<<<<<<<<<<STARTING>>>>>>>>>>>>>>>>>>>>>>>");
        new DbService().createDb();

        System.in.read();
        App.testCloseVertx();
    }

    public static void testJsonObjectCopy() {
        final JsonObject name = new JsonObject().put("name", "rasel");
        final JsonObject id = new JsonObject().put("id", 1);
        final JsonObject brand = new JsonObject().put("brand", "batb").put("address", new JsonObject().put("house", "HO-102"));
        name.put("id", id).put("brand", brand);
        System.out.println(name.encodePrettily());
        final JsonObject name2 = name.copy();
        System.out.println(name2.getJsonObject("id") == id);
        System.out.println(name2.getJsonObject("brand") == brand);
        System.out.println(name2.getJsonObject("brand").getJsonObject("address") == brand.getJsonObject("address"));
    }
}
