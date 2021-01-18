package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Property;

public class PropertyReceivedEvent extends BaseNetworkEvent<Property> {

    private Integer itemId;

    public PropertyReceivedEvent(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItemId() {
        return itemId;
    }
}
