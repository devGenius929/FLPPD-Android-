package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Message;

public class ConnectionAcceptedEvent extends BaseNetworkEvent<Message> {

    private Integer userId;

    public ConnectionAcceptedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
