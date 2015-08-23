package io.crm;

/**
 * Created by someone on 18/08/2015.
 */
public enum Indexes {
    region_name(mc.regions, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    area_name(mc.areas, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    house_name(mc.distribution_houses, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    brand_name(mc.brands, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    location_name(mc.locations, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    user_id(mc.user_indexes, new IndexTouple[]{new IndexTouple(Query.userId, 1)}, true),
    user_type(mc.user_types, new IndexTouple[]{new IndexTouple(Query.name, 1)}, true),
    username(mc.user_indexes, new IndexTouple[]{new IndexTouple(Query.username, 1)}, true);

    public final String collection;
    public final IndexTouple[] kyes;
    public final boolean unique;

    Indexes(mc collection, IndexTouple[] kyes, boolean unique) {
        this.collection = collection + "";
        this.kyes = kyes;
        this.unique = unique;
    }
}

class Query {
    public static final String name = "name";
    public static final String userId = "userId";
    public static final String username = "username";
    public static final String userType = "userType";
}
