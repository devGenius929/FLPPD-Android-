package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Message;

public class ConnectionDeletedEvent extends BaseNetworkEvent<Message> {

    private Integer userId;

    public ConnectionDeletedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
