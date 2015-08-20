package io.crm;

/**
 * Created by sohan on 7/29/2015.
 */
public enum mc {
    areas,
    brands,
    clients,
    consumer_contacts,
    consumers,
    distribution_houses,
    employees,
    regions,
    locations,
    user_indexes(-1),
    all_ids(-1);

    private long nextId = 0;

    mc(long nextId) {
        this.nextId = nextId;
    }

    mc() {
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
