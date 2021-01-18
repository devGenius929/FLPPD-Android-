package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Property;

public class PropertyDeletedEvent extends BaseNetworkEvent<Property> {

    private Integer itemId;

    public PropertyDeletedEvent(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItemId() {
        return itemId;
    }
}
