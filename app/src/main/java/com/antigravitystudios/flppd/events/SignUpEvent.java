package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

import retrofit2.Response;

public class SignUpEvent extends BaseNetworkEvent<User> {

    public SignUpEvent(Response<User> response) {
        super(response);
    }

    public SignUpEvent(Throwable t) {
        super(t);
    }
}
