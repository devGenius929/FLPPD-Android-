package com.antigravitystudios.flppd.events;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public class PubNubPresenceEvent {

    private PNPresenceEventResult presence;

    public PubNubPresenceEvent(PNPresenceEventResult presence) {
        this.presence = presence;
    }

    public PNPresenceEventResult getPresence() {
        return presence;
    }
}
