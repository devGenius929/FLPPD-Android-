package com.antigravitystudios.flppd.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmChatChannel extends RealmObject {

    @PrimaryKey
    private String channel_id;
    private RealmUser user;
    private String lastMessage;
    private String lastImageUrl;
    private Long time;
    private boolean read;

    public RealmUser getUser() {
        return user;
    }

    public void setUser(RealmUser user) {
        this.user = user;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastImageUrl() {
        return lastImageUrl;
    }

    public void setLastImageUrl(String lastImageUrl) {
        this.lastImageUrl = lastImageUrl;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
