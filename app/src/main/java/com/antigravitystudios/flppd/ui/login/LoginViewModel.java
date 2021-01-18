package com.antigravitystudios.flppd.ui.login;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.LoginEvent;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.WebviewActivity;
import com.antigravitystudios.flppd.ui.forgotPassword.ForgotPasswordActivity;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.antigravitystudios.flppd.ui.signup.SignUpActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginViewModel extends BaseActivityViewModel<LoginActivity> {

    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    public ObservableBoolean rememberUser = new ObservableBoolean();
    public ObservableBoolean loading = new ObservableBoolean();

    public LoginViewModel(LoginActivity activity) {
        super(activity);
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    private void attemptLogin(String email, String password) {

        boolean cancel = false;

        if (!isPasswordValid(password)) {
            passwordError.set(activity.getString(R.string.invalid_password));
            cancel = true;
        }

        if (!isEmailValid(email)) {
            emailError.set(activity.getString(R.string.invalid_email));
            cancel = true;
        }

        if (!cancel) {
            loading.set(true);
            NetworkModule.getInteractor().login(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResult(LoginEvent event) {
        if(event.isSuccessful()) {
            onLoginSuccess(event.getResult());
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    public void onLoginSuccess(User user) {
        SPreferences.saveSession(user.getAuthToken(), user, rememberUser.get());
        
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public void onSignInClick() {
        activity.hideKeyboard();
        passwordError.set("");
        emailError.set("");
        attemptLogin(email.get(), password.get());
    }

    public void onForgotClick() {
        Intent intent = new Intent(activity, ForgotPasswordActivity.class);
        activity.startActivity(intent);
    }

    public void onDontHaveAccountClick() {
        activity.startActivity(new Intent(activity, SignUpActivity.class));
        activity.finish();
    }

    public void onTermsClick() {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra("title", activity.getString(R.string.terms_and_conditions));
        intent.putExtra("href", activity.getString(R.string.terms_and_conditions_url));
        activity.startActivity(intent);
    }
}
