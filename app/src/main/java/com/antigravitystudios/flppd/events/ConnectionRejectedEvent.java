package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Message;

public class ConnectionRejectedEvent extends BaseNetworkEvent<Message> {

    private Integer userId;

    public ConnectionRejectedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
