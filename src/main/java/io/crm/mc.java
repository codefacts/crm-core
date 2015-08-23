package io.crm;

/**
 * Created by sohan on 7/29/2015.
 */
public enum mc {
    areas("area", "Area"),
    brands("brand", "Region"),
    clients("client", "Client"),
    consumer_contacts("consumer_contact", "Consumer Contact"),
    consumers("consumer", "Consumer"),
    distribution_houses("distributionHouse", "Distribution House"),
    employees("employee", "Employee"),
    regions("region", "Region"),
    locations("location", "Location"),
    user_types("userType", "User Type"),
    user_indexes("user_index", "User Index", -1, true);

    public final String label;
    public final String fieldName;
    private final boolean internal;
    private long nextId = 0;

    mc(String fieldName, String label, long nextId, boolean internal) {
        this.fieldName = fieldName;
        this.label = label;
        this.nextId = nextId;
        this.internal = internal;
    }

    mc(String fieldName, String label, long nextId) {
        this.fieldName = fieldName;
        this.label = label;
        this.nextId = nextId;
        this.internal = false;
    }

    mc(String fieldName, String label) {
        this.fieldName = fieldName;
        this.label = label;
        this.internal = false;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setNextId(long nextId) {
        this.nextId = nextId;
    }

    public long getNextId() {
        return nextId;
    }

    public long incrementNextId() {
        return nextId++;
    }

    public long decrementNextId() {
        return nextId--;
    }

    public long decrementNextId(long amount) {
        return nextId -= amount;
    }

    public long incrementNextId(long amount) {
        return nextId += amount;
    }

    public boolean isIdTypeLong() {
        return nextId >= 0;
    }
}