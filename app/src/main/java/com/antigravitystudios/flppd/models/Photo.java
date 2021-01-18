package com.antigravitystudios.flppd.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("image")
    private String image;

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}