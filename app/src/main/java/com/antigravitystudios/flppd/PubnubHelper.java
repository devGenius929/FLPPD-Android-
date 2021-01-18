package com.antigravitystudios.flppd;

import com.antigravitystudios.flppd.events.PubNubMessageEvent;
import com.antigravitystudios.flppd.events.PubNubPresenceEvent;
import com.antigravitystudios.flppd.models.ChatMessage;
import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

public class PubnubHelper {

    private static EventBus bus = EventBus.getDefault();
    private static Gson gson = new Gson();
    private static PubNub pubnub;
    private static String inboundChannel;

    public static void init() {
        int userId = SPreferences.getUser().getUserId();

        inboundChannel = "user_" + userId + "_inbound";

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-1f08232a-f179-11e7-9869-a6bd95f83dd1");
        pnConfiguration.setPublishKey("pub-c-d5c12415-2e3b-4701-b7ac-d75fa0d82af7");
        pnConfiguration.setUuid(String.valueOf(userId));
        pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubnub = new PubNub(pnConfiguration);

        addPushNotifications(SPreferences.getPushToken());

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                if (message.getChannel().equals(inboundChannel)) {
                    ChatMessage chatMessage = gson.fromJson(message.getMessage(), ChatMessage.class);

                    if(chatMessage.getChannel_id() == null) return;

                    chatMessage.setTime(message.getTimetoken());
                    MyApplication.getInstance().updateChatChannel(chatMessage);
                } else {
                    bus.post(new PubNubMessageEvent(message));
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                bus.post(new PubNubPresenceEvent(presence));
            }
        });

        pubnub.subscribe()
                .channels(Arrays.asList(inboundChannel))
                .execute();

    }

    public static void destroy() {
        pubnub.destroy();
        pubnub = null;
    }

    public static PubNub getPubnub() {
        return pubnub;
    }

    public static void addPushNotifications(String token) {
        if (token == null || pubnub == null) {
            return;
        }
        pubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Arrays.asList(inboundChannel))
                .deviceId(token)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {

                    }
                });
    }
}
