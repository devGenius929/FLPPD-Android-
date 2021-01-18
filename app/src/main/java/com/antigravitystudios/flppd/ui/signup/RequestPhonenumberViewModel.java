package com.antigravitystudios.flppd.ui.signup;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import com.antigravitystudios.flppd.PhonenumberKeyListener;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.events.ProfileUpdateEvent;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RequestPhonenumberViewModel extends BaseActivityViewModel<RequestPhonenumberActivity> {

    private Gson gson = new Gson();

    public ObservableField<String> phonenumber = new ObservableField<>();
    public ObservableField<String> phonenumberError = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean();

    public View.OnKeyListener phoneKeyListener;

    private User user;

    public RequestPhonenumberViewModel(RequestPhonenumberActivity activity) {
        super(activity);

        phoneKeyListener = new PhonenumberKeyListener();
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    private void savePhonenumber(String phonenumber) {

        boolean cancel = false;

        if (!isPhonenumberValid(phonenumber)) {
            phonenumberError.set(activity.getString(R.string.invalid_phonenumber));
            cancel = true;
        }

        if (!cancel) {
            loading.set(true);
            user.setPhoneNumber(phonenumber);
            NetworkModule.getInteractor().updateUserProfile(user);
        }
    }

    private boolean isPhonenumberValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSavePhonenumberResult(ProfileUpdateEvent event) {
        if(event.isSuccessful()) {
            onSavePhonenumberSuccess();
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    public void onSavePhonenumberSuccess() {
        Intent data = new Intent();
        data.putExtra("user", gson.toJson(user));
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    public void onNextClick() {
        activity.hideKeyboard();
        phonenumberError.set("");
        savePhonenumber(phonenumber.get());
    }

    public void setUserJson(String userJson) {
        user = gson.fromJson(userJson, User.class);
    }
}
