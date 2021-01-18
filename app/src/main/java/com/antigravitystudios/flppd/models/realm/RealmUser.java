package com.antigravitystudios.flppd.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUser extends RealmObject {

    @PrimaryKey
    private Integer user_id;
    private String name;
    private String avatar;
    private Long time_updated;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getTime_updated() {
        return time_updated;
    }

    public void setTime_updated(Long time_updated) {
        this.time_updated = time_updated;
    }
}
