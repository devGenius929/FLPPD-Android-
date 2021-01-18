package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Property;

import retrofit2.Response;

public class PropertyUpdateEvent extends BaseNetworkEvent<Property> {

    public PropertyUpdateEvent(Response<Property> response) {
        super(response);
    }

    public PropertyUpdateEvent(Throwable t) {
        super(t);
    }

    public PropertyUpdateEvent() {

    }
}
