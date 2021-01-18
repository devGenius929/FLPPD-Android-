package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Connection;

import java.util.List;

public class ConnectionsListReceivedEvent extends BaseNetworkEvent<List<Connection>> {

    private Integer userId;

    public ConnectionsListReceivedEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
