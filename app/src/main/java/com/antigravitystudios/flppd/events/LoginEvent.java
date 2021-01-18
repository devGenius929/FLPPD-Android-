package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

import retrofit2.Response;

public class LoginEvent extends BaseNetworkEvent<User> {

    public LoginEvent(Response<User> response) {
        super(response);
    }

    public LoginEvent(Throwable t) {
        super(t);
    }
}
