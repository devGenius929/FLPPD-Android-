package com.antigravitystudios.flppd.events;

import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

public class PubNubMessageEvent {

    private PNMessageResult message;

    public PubNubMessageEvent(PNMessageResult message) {
        this.message = message;
    }

    public PNMessageResult getMessage() {
        return message;
    }
}
