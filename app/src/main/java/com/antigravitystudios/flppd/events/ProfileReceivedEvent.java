package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

public class ProfileReceivedEvent extends BaseNetworkEvent<User> {

    private Integer userId;

    public ProfileReceivedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
