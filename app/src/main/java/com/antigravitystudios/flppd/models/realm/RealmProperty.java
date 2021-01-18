package com.antigravitystudios.flppd.models.realm;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manuel on 28/7/2017.
 */

public class RealmProperty extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String nickname;

    private String street;

    private String city;

    private String state;

    private String zip_code;

    private String  nbeds;

    private String  nbath;

    private String description;

    private Float sqft;

    @Ignore
    private Float latitude;

    @Ignore
    private Float longitude;

    private Integer  property_type_id;

    private String  property_type;

    private String parking;

    private int year_built;

    private Float lot_size;

    private boolean zoning;
    private RealmList<RealmPhoto> photos;

    private RealmWorksheetFlip realmWorksheetFlip;

    private RealmWorksheetRental realmWorksheetRental;

    public RealmProperty() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getNbeds() {
        return nbeds;
    }

    public void setNbeds(String nbeds) {
        this.nbeds = nbeds;
    }

    public String getNbath() {
        return nbath;
    }

    public void setNbath(String nbath) {
        this.nbath = nbath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getSqft() {
        return sqft;
    }

    public void setSqft(Float sqft) {
        this.sqft = sqft;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Integer getProperty_type_id() {
        return property_type_id;
    }

    public void setProperty_type_id(Integer property_type_id) {
        this.property_type_id = property_type_id;
    }

    public RealmList<RealmPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<RealmPhoto> photos) {
        this.photos = photos;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public int getYear_built() {
        return year_built;
    }

    public void setYear_built(int year_built) {
        this.year_built = year_built;
    }

    public Float getLot_size() {
        return lot_size;
    }

    public void setLot_size(Float lot_size) {
        this.lot_size = lot_size;
    }

    public boolean isZoning() {
        return zoning;
    }

    public void setZoning(boolean zoning) {
        this.zoning = zoning;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public RealmWorksheetFlip getRealmWorksheetFlip() {
        return realmWorksheetFlip;
    }

    public void setRealmWorksheetFlip(RealmWorksheetFlip realmWorksheetFlip) {
        this.realmWorksheetFlip = realmWorksheetFlip;
    }

    public RealmWorksheetRental getRealmWorksheetRental() {
        return realmWorksheetRental;
    }

    public void setRealmWorksheetRental(RealmWorksheetRental realmWorksheetRental) {
        this.realmWorksheetRental = realmWorksheetRental;
    }
}
