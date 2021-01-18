package com.antigravitystudios.flppd;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.antigravitystudios.flppd.models.ChatMessage;
import com.antigravitystudios.flppd.models.realm.RealmChatChannel;
import com.antigravitystudios.flppd.models.realm.RealmUser;
import com.antigravitystudios.flppd.networking.NetworkModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private static MyApplication app;
    private String currentChatChannelId;

    public static MyApplication getInstance() {
        return app;
    }

    public void onCreate() {
        super.onCreate();

        app = this;

        SPreferences.init(this);
        NetworkModule.init();
        Realm.init(this);
        BillingManager.init();

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("3.realm")
                .build();

        Realm.setDefaultConfiguration(config);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("flppd_messages", name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateChatChannel(ChatMessage chatMessage) {
        boolean read = false;
        if (currentChatChannelId != null && currentChatChannelId.equals(chatMessage.getChannel_id()))
            read = true;

        try (Realm realm = Realm.getDefaultInstance()) {
            RealmUser user;
            long actualTime = 0;

            RealmChatChannel actualChatChannel = realm.where(RealmChatChannel.class).equalTo("channel_id", chatMessage.getChannel_id()).findFirst();
            if (actualChatChannel != null) {
                actualTime = actualChatChannel.getTime();
                user = actualChatChannel.getUser();
            } else {
                user = realm.where(RealmUser.class).equalTo("user_id", chatMessage.getSender_id()).findFirst();
                if (user == null) {
                    user = new RealmUser();
                    user.setUser_id(chatMessage.getSender_id());
                }
            }

            if (chatMessage.getTime() > actualTime) {
                final RealmChatChannel chatChannel = new RealmChatChannel();
                chatChannel.setUser(user);
                chatChannel.setChannel_id(chatMessage.getChannel_id());
                chatChannel.setTime(chatMessage.getTime());
                chatChannel.setLastImageUrl(chatMessage.getImage_url());
                chatChannel.setLastMessage(chatMessage.getMessage());
                chatChannel.setRead(read);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(chatChannel);
                    }
                });
            }
        }
    }

    public String getCurrentChatChannelId() {
        return currentChatChannelId;
    }

    public void setCurrentChatChannelId(String currentChatChannelId) {
        this.currentChatChannelId = currentChatChannelId;

        if (currentChatChannelId != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                final RealmChatChannel actualChatChannel = realm.where(RealmChatChannel.class).equalTo("channel_id", currentChatChannelId).findFirst();
                if (actualChatChannel != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            actualChatChannel.setRead(true);
                        }
                    });
                }
            }
        }
    }

    public void updatePushToken(String token) {
        SPreferences.setPushToken(token);
    }

/*    public boolean checkSubscrptionActive() {

    }

    public void subscribe() {

    }*/
}