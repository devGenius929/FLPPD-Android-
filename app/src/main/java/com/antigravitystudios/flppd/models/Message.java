package com.antigravitystudios.flppd.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @Expose
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
