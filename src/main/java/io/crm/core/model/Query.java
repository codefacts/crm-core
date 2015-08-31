package io.crm.core.model;

/**
 * Created by someone on 18/08/2015.
 */
public class Query {

    public static final String id = "_id";
    public static final String createDate = "createDate";
    public static final String modifyDate = "modifyDate";
    public static final String createdBy = "createdBy";
    public static final String modifiedBy = "modifiedBy";
    public static final String __self = "__self";
    public static final String exists = "exists";
    public static final String withId = "withId";
    public static final String name = "name";

    public static final String system_indexes = "system.indexes";
    public static final String indexes = "indexes";
    public static final String unique = "unique";
    public static final String key = "key";
    public static final String username = "username";
    public static final String field = "field";
    public static final String message = "message";
    public static final String region = "region";
    public static final String $set = "$set";
    public static final String areaName = "areaName";
    public static final String userId = "userId";
    public static final String assigned_id = "assigned_id";
    public static final String area = "area";
    public static final String distributionHouse = "distributionHouse";
    public static final String distributionHouseId = "distributionHouseId";
    public static final String label = "label";

    public static final String regionId = Area.region + "." + id;
    public static final String areaId = House.area + "." + id;
    public static final String houseId = concat(distributionHouse, id);
    public static final String userTypeId = concat(User.userType, id);
    public static final String brId = concat(Contact.br, id);
    public static final String prefix = "prefix";
    public static final String brand = "brand";

    public static final String concat(String... strings) {
        return String.join(".", strings);
    }
}
