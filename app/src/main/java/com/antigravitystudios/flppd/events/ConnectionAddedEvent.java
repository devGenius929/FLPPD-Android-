package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Message;

public class ConnectionAddedEvent extends BaseNetworkEvent<Message> {

    private Integer userId;

    public ConnectionAddedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
