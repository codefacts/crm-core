package io.crm.core.service;

import io.crm.core.App;
import io.crm.core.exceptions.ValidationException;
import io.crm.core.helper.Helper;
import io.crm.mc;
import io.crm.core.mm;
import io.crm.model.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static io.crm.QC.id;
import static io.crm.model.Region.*;
import static io.crm.model.Region.name;
import static io.crm.util.ExceptionUtil.*;
import static io.crm.util.Util.*;

/**
 * Created by sohan on 8/1/2015.
 */
@Service
public class ImportService {
    public final static int defaultBatchSize = 1000;
    private static final String BRANDS = "BRANDS";
    private static final String SUP_AC_RELATION = "SUP_AC_RELATION";
    private static final String BR_CATEGORIES = "BR_CATEGORIES";
    private static final String REGIONS = "REGIONS";
    private static final String AREAS = "AREAS";
    private static final String DISTRIBUTION_HOUSES = "DISTRIBUTION_HOUSES";
    private static final String USERS = "USERS";
    private static final String BRS = "BRS";
    private static final String SMS_INBOX = "SMS_INBOX";
    private final String dir = "D:\\DotNetProjects\\verify\\CRM_DB_EXPORTER\\CRM_DB_EXPORTER\\App_Data\\Wills_Kings_Launch_May_2015\\tables";

    private final App app;

    @Autowired
    public ImportService(App app) {
        this.app = app;
    }


    public void importDb() {

        app.getMongoClient().runCommand(mm.dropDatabase, new JsonObject().put(mm.dropDatabase, 1), r -> {
            if (r.failed()) {
                throw new RuntimeException(r.cause());
            }

            toRuntime(this::loadData);
        });
    }

    private void loadData() throws IOException {

        final JsonObject campaign = new JsonObject();
        final Holder holder = new Holder(campaign);

        final JsonArray users = allUsers(holder);

        insert(mc.areas, holder.areaList.values());
        insert(mc.regions, holder.regionList.values());
        insert(mc.distribution_houses, holder.houseList.values());
        insert(mc.brands, holder.brandList.values());

        insert(mc.locations, holder.locationList.values());
        insert(mc.employees, users);

        final String[] files = new File(dir).list((f, name) -> name.startsWith(SMS_INBOX));
        for (String f : files) {
            final JsonArray contactList = contactList(f, campaign, holder);
            insert(mc.consumer_contacts, contactList);
        }
    }

    private void insert(mc col, Collection<JsonObject> values) {
        String colName = col + "";
        app.getMongoClient().runCommand(mm.insert,
                new JsonObject().put(mm.insert, colName)
                        .put(mm.documents, toJsonArray(values)), s -> {
                    if (s.failed()) {
                        System.out.println("Insertion failed. Collection = " + colName);
                        s.cause().printStackTrace();
                        return;
                    }
                    System.out.println("Insertion successful. Collection = " + colName);
                });
    }

    private void insert(final mc c, final JsonArray array) {
        String colName = c + "";
        final int size = array.size();

        for (int start = 0; start < size; start += defaultBatchSize) {

            int toIndex = start + defaultBatchSize;
            toIndex = (toIndex > size) ? size : toIndex;
            final JsonArray jsonArray = new JsonArray(array.getList().subList(start, toIndex));

            app.getMongoClient().runCommand(mm.insert,
                    new JsonObject().put(mm.insert, colName)
                            .put(mm.documents, jsonArray), s -> {
                        if (s.failed()) {
                            System.out.println("Insertion failed. Collection = " + colName);
                            s.cause().printStackTrace();
                            return;
                        }
                        System.out.println("Insertion successful. Collection = " + colName);
                    });
        }
    }

    private JsonArray toJsonArray(Collection<JsonObject> values) {
        final JsonArray array = new JsonArray();
        for (JsonObject v : values) {
            array.add(v);
        }
        return array;
    }

    private JsonArray allUsers(Holder holder) {

        final JsonArray array = new JsonArray();

        holder.userList.forEach((k, user) -> {
            if (Helper.employeeType(user) == EmployeeType.br_supervisor) {
                final Long actualId = user.getLong(id);
                final Long sup_id = holder.ac_sup_relation.get(actualId);
                user.put(Sup.ac, holder.userList.get(sup_id));
            }
            array.add(user);
        });

        holder.brList.forEach((k, user) -> {
            array.add(user);
        });
        return array;
    }

    private final class Holder {
        final Map<Long, JsonObject> brandList;
        final Map<Long, Long> ac_sup_relation;
        final Map<Long, JsonObject> regionList;
        final Map<Long, JsonObject> areaList;
        final Map<Long, JsonObject> houseList;
        final Map<Long, JsonObject> brCategories;
        final Map<Long, JsonObject> brList;
        final Map<Long, JsonObject> userList;
        final Map<Long, JsonObject> locationList;
        final Map<String, JsonObject> locationMap = new HashMap<>();
        final Map<Long, Set<String>> locationMapByHouse = new HashMap<>();

        Holder(final JsonObject campaign) {
            try {
                final UserIdGenerator userIdGenerator = new UserIdGenerator();
                brandList = brandList(BRANDS);
                ac_sup_relation = acSupRelation(SUP_AC_RELATION);
                brCategories = brCategories(BR_CATEGORIES);
                regionList = regionList(REGIONS);
                areaList = areaList(AREAS, regionList);
                houseList = houseList(DISTRIBUTION_HOUSES, areaList, locationMap, locationMapByHouse);
                locationList = locationList(BRS, locationMap, locationMapByHouse, houseList);
                userList = userList(USERS, campaign, userIdGenerator);
                brList = brList(BRS, houseList, locationMap, brCategories, userList, userIdGenerator);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Map<Long, Long> acSupRelation(String fileName) throws IOException {
        final HashMap<Long, Long> map = new HashMap<>();
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
        for (Object jsonObj : jsonArray) {
            final JsonObject json = (JsonObject) jsonObj;
            final Long sup_id = json.getLong("SUP_ID");
            final Long ac_id = json.getLong("AC_ID");
            map.put(sup_id, ac_id);
        }
        return map;
    }

    private Map<Long, JsonObject> brCategories(String fileName) throws IOException {
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
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

    private Map<Long, JsonObject> locationList(String fileName, Map<String, JsonObject> hashMap, Map<Long, Set<String>> locationMapByHouse, Map<Long, JsonObject> houseList) throws IOException {
        final JsonArray jsonArray = new JsonArray(loadFile(fileName));
        long location_id = 1;
        for (Object jsonObj : jsonArray) {
            final JsonObject json = (JsonObject) jsonObj;
            final String location = json.getString("LOCATION");
            final Long distribution_house_id = json.getLong("DISTRIBUTION_HOUSE_ID");
            if (!hashMap.containsKey(location)) {
                final JsonObject locationObject = new JsonObject()
                        .put(id, location_id++)
                        .put(name, location)
                        .put(Location.distributionHouse, newHouse(houseList, distribution_house_id));

                hashMap.put(location, locationObject);
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

    private Map<Long, JsonObject> brandList(String fileName) throws IOException {
        JsonArray regions = new JsonArray(loadFile(fileName));
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

    private Map<Long, JsonObject> houseList(String fileName, Map<Long, JsonObject> areaList, Map<String, JsonObject> locationMap, Map<Long, Set<String>> locationMapByHouse) throws IOException {
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            final Long distribution_house_id = json.getLong("DISTRIBUTION_HOUSE_ID");
            final Long area_id = json.getLong("AREA_ID");
            JsonObject entity = new JsonObject().put(id, distribution_house_id)
                    .put(name, json.getString("DISTRIBUTION_HOUSE_NAME"))
                    .put(House.area, areaList.get(area_id))
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

    private Map<Long, JsonObject> areaList(String fileName, Map<Long, JsonObject> regionList) throws IOException {
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {
            JsonObject json = (JsonObject) jsonObj;
            Long region_id = json.getLong("REGION_ID");
            JsonObject entity = new JsonObject().put(id, json.getLong("AREA_ID"))
                    .put(name, json.getString("AREA_NAME"))
                    .put(Area.region, regionList.get(region_id))
                    .put(active, true);
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private Map<Long, JsonObject> regionList(String fileName) throws IOException {
        JsonArray regions = new JsonArray(loadFile(fileName));
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

    private Map<Long, JsonObject> userList(String fileName, JsonObject campaign, UserIdGenerator userIdGenerator) throws IOException {
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
        Map<Long, JsonObject> list = new HashMap<>();
        for (Object jsonObj : jsonArray) {

            JsonObject json = (JsonObject) jsonObj;
            final Long userId = json.getLong("USER_ID");
            final Long user_type_id = json.getLong("USER_TYPE");
            EmployeeType employeeType = null;

            try {
                employeeType = userType(user_type_id);
            } catch (NoSuchElementException ex) {
                throw new NoSuchElementException(String.format("No UserType defined for user {id: %d, name: %s} for userTypeId = %d", userId, json.getString("EMPLOY_NAME"), user_type_id));
            }

            JsonObject user = new JsonObject()
                    .put(id, userId)
                    .put(User.userId, userIdGenerator.nextId(user_type_id))
                    .put(User.userType, userTypeObj(employeeType))
                    .put(User.username, json.getString("USER_NAME"))
                    .put(User.name, json.getString("EMPLOY_NAME"))
                    .put(User.mobile, json.getString("MOBILE"))
                    .put(User.password, json.getString("password"))
                    .put(active, json.getInteger("ACTIVE"));

            if (employeeType == EmployeeType.br_supervisor) {
                user.put(Sup.campaign, campaign);
            }

            list.put(user.getLong(id), user);
        }
        return list;
    }

    private Map<Long, JsonObject> brList(String fileName, Map<Long, JsonObject> houseList, Map<String, JsonObject> locationMap, Map<Long, JsonObject> brCategories, Map<Long, JsonObject> userList, UserIdGenerator userIdGenerator) throws IOException {
        JsonArray jsonArray = new JsonArray(loadFile(fileName));
        Map<Long, JsonObject> list = new HashMap<>();

        for (Object jsonObj : jsonArray) {

            final JsonObject json = (JsonObject) jsonObj;
            final JsonObject house = newHouse(houseList, json.getLong("DISTRIBUTION_HOUSE_ID"));
            final JsonObject category = newCategory(brCategories, json.getLong("BR_CATEGORY_ID"));
            final Long supId = json.getLong("BR_SUP_ID");
            final JsonObject supervisor = newSupervisor(userList, supId);

            final String join_date = json.getString("JOIN_DATE");
            final String resign_date = json.getString("RESIGN_DATE");
            final JsonObject joinDate = (join_date == null
                    || join_date.trim().isEmpty()) ? null : toMongoDate(join_date);
            final JsonObject resignDate = (resign_date == null
                    || resign_date.isEmpty()) ? null : toMongoDate(resign_date);

            JsonObject entity = new JsonObject().put(id, json.getLong("BR_ID"))
                    .put(name, json.getString("BR_NAME"))
                    .put(User.userId, userIdGenerator.nextId(EmployeeType.br.id))
                    .put(User.userType, userTypeObj(EmployeeType.admin.br))
                    .put(Br.distributionHouse, house)
                    .put(Br.mobile, json.getString("BR_MOBILE_1"))
                    .put(Br.supervisor, supervisor)
                    .put(Br.category, category)
                    .put(Br.location, newLocation(locationMap, json.getString("LOCATION")))
                    .put(Br.joinDate, sallowCall(() -> joinDate))
                    .put(Br.resignDate, sallowCall(() -> resignDate))
                    .put(Br.remarks, json.getString("REMARKS"))
                    .put(active, json.getInteger("ACTIVE"));
            list.put(entity.getLong(id), entity);
        }
        return list;
    }

    private JsonObject newSupervisor(Map<Long, JsonObject> userList, Long supId) {
        final JsonObject object = userList.get(supId);
        return object != null ? object : null;
    }

    private JsonArray contactList(String absoluteFilePath, JsonObject campaign, Holder holder) throws IOException {
        final JsonArray jsonArray = new JsonArray(loadFile2(absoluteFilePath));
        final JsonArray list = new JsonArray();
        JsonArray contacts = new JsonArray();
        final JsonObject cmp = campaign.copy();

        for (final Object jsonObj : jsonArray) {
            try {
                final JsonObject json = (JsonObject) jsonObj;

                final Long sms_id = json.getLong("SMS_ID");
                final Long br_id = json.getLong("BR_ID");
                JsonObject br = holder.brList.get(br_id);
                JsonObject house;
                JsonObject area;
                JsonObject region;
                if (br == null) {
                    throw new ValidationException(String.format("BR_ID: %d not found. SMS ID: %d", br_id, sms_id));
                }

                br = br.copy();

                house = newHouse(holder.houseList, br.getJsonObject(Br.distributionHouse).getLong(id)).copy();
                area = newArea(holder.areaList, house).copy();
                region = newRegion(holder.regionList, area).copy();

                br.put(Br.distributionHouse, house);
                house.put(House.area, area).put(House.locations,
                        findLocations(holder.locationMap, holder.locationMapByHouse, house.getLong(id))
                                .copy());
                area.put(Area.region, region);

                if (br.getJsonObject(Br.supervisor) != null && br.getJsonObject(Br.supervisor).getLong(id) != null) {
                    JsonObject supervisor = newSupervisor(holder.userList, br.getJsonObject(Br.supervisor).getLong(id))
                            .copy();
                    br.put(Br.supervisor, supervisor);
                }

                final String date1 = json.getString("Date");
                final String receive_date = json.getString("RECEIVE_DATE");
                final JsonObject date = (date1 == null || date1.isEmpty()) ? null : toMongoDate(date1);
                final JsonObject receiveDate = (receive_date == null || receive_date.isEmpty()) ? null : toMongoDate(receive_date);

                //Brand
                JsonObject entity = new JsonObject()
                        .put(id, sms_id)

                        .put(Contact.br, br)
                        .put(Contact.campaign, cmp)
                        .put(Contact.consumer, consumer(json))

                        .put(Contact.brand, json.getString("Brand"))
                        .put(Contact.ptr, json.getBoolean("PTR"))
                        .put(Contact.swp, json.getBoolean("SWAP"))
                        .put(Contact.date, date)
                        .put(Contact.receive_date, receiveDate);
                list.add(entity);
                contacts.add(entity);
            } catch (ValidationException ex) {
                System.out.println(">>>>>VALIDATION EXCEPTION: " + ex.getMessage());
            }
        }
        return list;
    }

    private JsonObject consumer(JsonObject json) {
        return new JsonObject()
                .put(Contact.name, json.getString("Customer_Name"))
                .put(Contact.mobile, json.getString("CONSUMER_MOBILE"))
                .put(Contact.fatherName, json.getString("FATHER_NAME"))
                .put(Contact.age, json.getInteger("Age"))
                .put(Contact.occupation, json.getString("Occupation"));
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
        return br_category_id == null ? null : brCategories.get(br_category_id);
    }

    private JsonObject newLocation(Map<String, JsonObject> locationList, String location) {
        return locationList.get(location);
    }

    private JsonObject newHouse(Map<Long, JsonObject> houseList, long houseId) {
        return houseList.get(houseId);
    }

    private JsonObject newArea(Map<Long, JsonObject> areaList, JsonObject house) {
        return areaList
                .get(house
                                .getJsonObject(House.area)
                                .getLong(id)
                );
    }

    private JsonObject newRegion(Map<Long, JsonObject> regionList, JsonObject area) {
        return regionList.get(area.getJsonObject(Area.region).getLong(id));
    }

    private String loadFile(final String fileName) throws IOException {
        try (final FileInputStream fis = new FileInputStream(new File(dir, fileName + ".json"))) {
            return IOUtils.toString(fis);
        }
    }

    private String loadFile2(final String absoluteFilePath) throws IOException {
        try (final FileInputStream fis = new FileInputStream(new File(dir, absoluteFilePath))) {
            return IOUtils.toString(fis);
        }
    }

    private static final class UserIdGenerator {
        final UserId ad = new UserId(EmployeeType.admin.prefix + "-");
        final UserId ho = new UserId(EmployeeType.head_office.prefix + "-");
        final UserId ac = new UserId(EmployeeType.area_coordinator.prefix + "-");
        final UserId sp = new UserId(EmployeeType.br_supervisor.prefix + "-");
        final UserId br = new UserId(EmployeeType.br.prefix + "-");
        final UserId co = new UserId(EmployeeType.call_operator.prefix + "-");
        final UserId cs = new UserId(EmployeeType.call_center_supervisor.prefix + "-");

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
                    throw new IllegalArgumentException(String.format("UsertypeId: %d not found. UserId could not be generated.", user_type_id));
            }
        }
    }

    private static final class UserId {
        final static Set<String> prefixes = new HashSet<>();
        final String prefix;
        long id = 1;

        private UserId(String prefix) {
            if (!prefixes.add(prefix)) {
                throw new IllegalArgumentException("Invalid user id prefix. This prefix '" + prefix + "' already assigned. Assigned Prifixes are : " + prefixes);
            }
            this.prefix = prefix;
        }

        public String nextId() {
            return String.format(prefix + "%05d", (id++));
        }
    }

    public static void main(String... args) throws IOException {


        System.out.println("<<<<<<<<<<<<<<<<<<<<STARTED>>>>>>>>>>>>>>>>>>>>>>>\n");


        System.in.read();

        System.out.println("\n<<<<<<<<<<<<<<<<<<<<DONE>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public static void testJsonObjectCopy() {
        final JsonObject name = new JsonObject().put("name", "rasel");
        final JsonObject id = new JsonObject().put("id", 1);
        final JsonObject brand = new JsonObject().put("brand", "batb").put("address", new JsonObject().put("house", "HO-102"));
        name.put("id", id).put("brand", brand);
        System.out.println(name.encodePrettily());
        final JsonObject name2 = name;
        System.out.println(name2.getJsonObject("id") == id);
        System.out.println(name2.getJsonObject("brand") == brand);
        System.out.println(name2.getJsonObject("brand").getJsonObject("address") == brand.getJsonObject("address"));
    }
}
