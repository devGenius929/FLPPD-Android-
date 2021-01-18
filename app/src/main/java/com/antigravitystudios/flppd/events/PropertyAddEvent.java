package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Property;

import retrofit2.Response;

public class PropertyAddEvent extends BaseNetworkEvent<Property> {

    public PropertyAddEvent(Response<Property> response) {
        super(response);
    }

    public PropertyAddEvent(Throwable t) {
        super(t);
    }

    public PropertyAddEvent() {

    }
}
