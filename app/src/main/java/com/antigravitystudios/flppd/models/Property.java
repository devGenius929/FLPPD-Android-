package com.antigravitystudios.flppd.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Property extends BaseObservable {

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("price")
    private String price;

    @Expose
    @SerializedName("arv")
    private String arv;

    @Expose
    @SerializedName("street")
    private String street;

    @Expose
    @SerializedName("city")
    private String city;

    @Expose
    @SerializedName("state")
    private String state;

    @Expose
    @SerializedName("zip_code")
    private String zip_code;

    @Expose
    @SerializedName("nbeds")
    private String nbeds;

    @Expose
    @SerializedName("nbath")
    private String nbath;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("sqft")
    private String sqft;

    @Expose
    @SerializedName("property_category")
    private String property_category;

    @Expose
    @SerializedName("number_unit")
    private String number_unit;

    @Expose
    @SerializedName("year_built")
    private String year_built;

    @Expose
    @SerializedName("parking")
    private String parking;

    @Expose
    @SerializedName("lot_size")
    private String lot_size;

    @Expose
    @SerializedName("zoning")
    private String zoning;

    @Expose
    @SerializedName("rental_rating")
    private String rental_rating;

    @Expose
    @SerializedName("created_at")
    private String created_at;

    @Expose
    @SerializedName("updated_at")
    private String updated_at;

    @Expose
    @SerializedName("latitude")
    private Float latitude;

    @Expose
    @SerializedName("longitude")
    private Float longitude;

    @Expose
    @SerializedName("price_currency")
    private String price_currency;

    @Expose
    @SerializedName("arv_currency")
    private String arv_currency;

    @Expose
    @SerializedName("price_to_human")
    private String price_to_human;

    @Expose
    @SerializedName("arv_to_human")
    private String arv_to_human;

    @Expose
    @SerializedName("property_type")
    private String property_type;

    @Expose
    @SerializedName("property_type_id")
    private Integer property_type_id;

    @Expose
    @SerializedName("property_listing_type")
    private String property_listing_type;

    @Expose
    @SerializedName("property_listing_id")
    private int property_listing_id;

    @Expose
    @SerializedName("created_at_in_words")
    private String created_at_in_words;

    @Expose
    @SerializedName("pubDate")
    private String pubDate;

    @Expose
    @SerializedName("default_img")
    private String default_img;

    @Expose
    @SerializedName("default_img_thumb")
    private String default_img_thumb;

    @Expose
    @SerializedName("default_img_thumb_port")
    private String default_img_thumb_port;

    @Expose
    @SerializedName("rehab_cost")
    private String rehab_cost;


    @Expose
    @SerializedName("starred")
    private Boolean starred;

    @Expose
    @SerializedName("user")
    private User user;

    @Expose
    @SerializedName("photo_data")
    private String photo_data;

    @Expose
    @SerializedName("photos")
    private ArrayList<Photo> photos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getArv() {
        return arv;
    }

    public void setArv(String arv) {
        this.arv = arv;
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

    public String getSqft() {
        return sqft;
    }

    public void setSqft(String sqft) {
        this.sqft = sqft;
    }

    public String getProperty_category() {
        return property_category;
    }

    public void setProperty_category(String property_category) {
        this.property_category = property_category;
    }

    public String getNumber_unit() {
        return number_unit;
    }

    public void setNumber_unit(String number_unit) {
        this.number_unit = number_unit;
    }

    public String getYear_built() {
        return year_built;
    }

    public void setYear_built(String year_built) {
        this.year_built = year_built;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getLot_size() {
        return lot_size;
    }

    public void setLot_size(String lot_size) {
        this.lot_size = lot_size;
    }

    public String getZoning() {
        return zoning;
    }

    public void setZoning(String zoning) {
        this.zoning = zoning;
    }

    public String getRental_rating() {
        return rental_rating;
    }

    public void setRental_rating(String rental_rating) {
        this.rental_rating = rental_rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public String getPrice_currency() {
        return price_currency;
    }

    public void setPrice_currency(String price_currency) {
        this.price_currency = price_currency;
    }

    public String getArv_currency() {
        return arv_currency;
    }

    public void setArv_currency(String arv_currency) {
        this.arv_currency = arv_currency;
    }

    public String getPrice_to_human() {
        return price_to_human;
    }

    public void setPrice_to_human(String price_to_human) {
        this.price_to_human = price_to_human;
    }

    public String getArv_to_human() {
        return arv_to_human;
    }

    public void setArv_to_human(String arv_to_human) {
        this.arv_to_human = arv_to_human;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public Integer getProperty_type_id() {
        return property_type_id;
    }

    public void setProperty_type_id(Integer property_type_id) {
        this.property_type_id = property_type_id;
    }

    public String getProperty_listing_type() {
        return property_listing_type;
    }

    public void setProperty_listing_type(String property_listing_type) {
        this.property_listing_type = property_listing_type;
    }

    public int getProperty_listing_id() {
        return property_listing_id;
    }

    public void setProperty_listing_id(int property_listing_id) {
        this.property_listing_id = property_listing_id;
    }

    //for spinner implementation
    @Bindable
    public int getListing_id() {
        return property_listing_id - 1;
    }

    public void setListing_id(int property_listing_id) {
        this.property_listing_id = property_listing_id + 1;
    }

    public String getCreated_at_in_words() {
        return created_at_in_words;
    }

    public void setCreated_at_in_words(String created_at_in_words) {
        this.created_at_in_words = created_at_in_words;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDefault_img() {
        return default_img;
    }

    public void setDefault_img(String default_img) {
        this.default_img = default_img;
    }

    public String getDefault_img_thumb() {
        return default_img_thumb;
    }

    public void setDefault_img_thumb(String default_img_thumb) {
        this.default_img_thumb = default_img_thumb;
    }

    public String getDefault_img_thumb_port() {
        return default_img_thumb_port;
    }

    public void setDefault_img_thumb_port(String default_img_thumb_port) {
        this.default_img_thumb_port = default_img_thumb_port;
    }

    public String getRehab_cost() {
        return rehab_cost;
    }

    public void setRehab_cost(String rehab_cost) {
        this.rehab_cost = rehab_cost;
    }

    public Boolean getStarred() {
        return starred;
    }

    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoto_data() {
        return photo_data;
    }

    public void setPhoto_data(String photo_data) {
        this.photo_data = photo_data;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}