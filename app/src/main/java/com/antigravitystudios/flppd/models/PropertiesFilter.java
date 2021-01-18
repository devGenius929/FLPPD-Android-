package com.antigravitystudios.flppd.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertiesFilter {

    @Expose
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("city")
    private String city;

    @Expose
    @SerializedName("state")
    private String state;

    @Expose
    @SerializedName("zipcode")
    private String zipcode;

    @Expose
    @SerializedName("price_min")
    private String price_min;

    @Expose
    @SerializedName("price_max")
    private String price_max;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPrice_min() {
        return price_min;
    }

    public void setPrice_min(String price_min) {
        this.price_min = price_min;
    }

    public String getPrice_max() {
        return price_max;
    }

    public void setPrice_max(String price_max) {
        this.price_max = price_max;
    }
}
