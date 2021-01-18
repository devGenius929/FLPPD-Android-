package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

import retrofit2.Response;

public class ProfileUpdateEvent extends BaseNetworkEvent<User> {

    public ProfileUpdateEvent(Response<User> response) {
        super(response);
    }

    public ProfileUpdateEvent(Throwable t) {
        super(t);
    }
}
