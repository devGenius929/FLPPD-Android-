package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Property;

import java.util.List;

public class PropertiesListReceivedEvent extends BaseNetworkEvent<List<Property>> {

    private String type;
    private Integer userId;

    public PropertiesListReceivedEvent(String type, Integer userId) {
        this.type = type;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public Integer getUserId() {
        return userId;
    }
}
