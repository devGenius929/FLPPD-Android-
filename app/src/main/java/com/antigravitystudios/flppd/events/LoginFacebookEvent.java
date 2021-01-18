package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

import retrofit2.Response;

public class LoginFacebookEvent extends BaseNetworkEvent<User> {

    public LoginFacebookEvent(Response<User> response) {
        super(response);
    }

    public LoginFacebookEvent(Throwable t) {
        super(t);
    }
}
