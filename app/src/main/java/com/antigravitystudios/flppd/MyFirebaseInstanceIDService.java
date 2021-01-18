package com.antigravitystudios.flppd;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        MyApplication.getInstance().updatePushToken(FirebaseInstanceId.getInstance().getToken());
    }

}
