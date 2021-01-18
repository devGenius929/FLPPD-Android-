package com.antigravitystudios.flppd.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatMessage {

    @SerializedName("channel_id")
    @Expose
    private String channel_id;

    @SerializedName("sender_id")
    @Expose
    private Integer sender_id;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("message_id")
    @Expose
    private String message_id;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("time")
    @Expose
    private Long time;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
