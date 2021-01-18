package com.antigravitystudios.flppd.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connection {

    @Expose
    @SerializedName("user")
    private User user;

    @Expose
    @SerializedName("mutualFriends")
    private Integer mutualFriends;

    public User getUser() {
        return user;
    }

    public Integer getMutualFriends() {
        return mutualFriends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        User thatUser;
        if(o instanceof User) thatUser = (User) o;
        else if(o instanceof Connection) thatUser = ((Connection) o).getUser();
        else return false;

        return user.equals(thatUser);
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }
}
