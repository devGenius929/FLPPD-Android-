package com.antigravitystudios.flppd.ui.home;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;

import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.LoginFacebookEvent;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.login.LoginActivity;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.antigravitystudios.flppd.ui.signup.RequestPhonenumberActivity;
import com.antigravitystudios.flppd.ui.signup.SignUpActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

public class HomeViewModel extends BaseActivityViewModel<HomeActivity> {

    private static final int RequestPhonenumberRC = 1;

    private Gson gson = new Gson();
    private CallbackManager callbackManager;

    public ObservableBoolean loading = new ObservableBoolean();

    public HomeViewModel(HomeActivity activity) {
        super(activity);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        attemptLoginFacebook(loginResult.getAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RequestPhonenumberRC && resultCode == RESULT_OK) {
            User user = gson.fromJson(data.getStringExtra("user"), User.class);
            onLoginSuccess(user);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSignInClick() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public void onSignUpClick() {
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivity(intent);
    }

    public void onFacebookClick() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    private void attemptLoginFacebook(String token) {
        loading.set(true);
        NetworkModule.getInteractor().facebook_auth(token);
    }

    public void onLoginSuccess(User user) {
        SPreferences.saveSession(user.getAuthToken(), user, true);

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginFacebookResult(LoginFacebookEvent event) {
        if (event.isSuccessful()) {
            if (event.getResult().getPhoneNumber() == null || TextUtils.isEmpty(event.getResult().getPhoneNumber())) {
                Intent intent = new Intent(activity, RequestPhonenumberActivity.class);
                intent.putExtra("user", gson.toJson(event.getResult()));
                activity.startActivityForResult(intent, RequestPhonenumberRC);
            } else {
                onLoginSuccess(event.getResult());
            }
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }
}
