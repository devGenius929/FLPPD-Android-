package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.Message;

import retrofit2.Response;

public class RecoverEvent extends BaseNetworkEvent<Message> {

    public RecoverEvent(Response<Message> response) {
        super(response);
    }

    public RecoverEvent(Throwable t) {
        super(t);
    }
}
