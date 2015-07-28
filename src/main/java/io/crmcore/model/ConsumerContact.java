package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "consumer_contacts")
public class ConsumerContact implements Serializable, Model {
    public static class Props {
        public static final String region = "region";
        public static final String area = "area";
        public static final String distributionHouse = "distributionHouse";
        public static final String br = "br";
        public static final String consumer = "consumer";
        public static final String brand = "brand";
        public static final String name = "name";
        public static final String fatherName = "fatherName";
        public static final String phone = "phone";
        public static final String occupation = "occupation";

        public static final String age = "age";
        public static final String date = "date";

        public static final String description = "description";

        public static final String ptr = "ptr";
        public static final String swp = "swp";

        public static final String latitude = "latitude";
        public static final String longitude = "longitude";
        public static final String accuracy = "accuracy";
    }

    public static final String col_contact_date = "contact_date";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Region region;
    @ManyToOne
    private Area area;
    @ManyToOne
    private DistributionHouse distributionHouse;
    @ManyToOne
    private Br br;

    @ManyToOne
    private Consumer consumer;
    @ManyToOne
    private Brand brand;
    @Column(columnDefinition = "varchar(250) NULL DEFAULT NULL")
    private String name;
    @Column(columnDefinition = "varchar(250) NULL DEFAULT NULL")
    private String fatherName;
    @Column(columnDefinition = "varchar(15) NULL DEFAULT NULL")
    private String phone;
    @Column(columnDefinition = "varchar(1000) NULL DEFAULT NULL")
    private String occupation;
    private int age;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "contact_date")
    private Date date;
    @Column(columnDefinition = "varchar(1000) null default null")
    private String description;

    boolean ptr;
    boolean swp;

    private double latitude;
    private double longitude;
    private double accuracy;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifyDate;
    @Column(length = 20, nullable = false)
    protected String createdBy;
    @Column(length = 20, nullable = false)
    protected String modifiedBy;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public static String getCol_contact_date() {
        return col_contact_date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public DistributionHouse getDistributionHouse() {
        return distributionHouse;
    }

    public void setDistributionHouse(DistributionHouse distributionHouse) {
        this.distributionHouse = distributionHouse;
    }

    public Br getBr() {
        return br;
    }

    public void setBr(Br br) {
        this.br = br;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPtr() {
        return ptr;
    }

    public boolean isSwp() {
        return swp;
    }

    public void setPtr(boolean ptr) {
        this.ptr = ptr;
    }

    public void setSwp(boolean swp) {
        this.swp = swp;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumerContact)) return false;

        ConsumerContact that = (ConsumerContact) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ConsumerContact{" +
                "id=" + id +
                ", br=" + br +
                ", brand=" + brand +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", date=" + date +
                '}';
    }
}
