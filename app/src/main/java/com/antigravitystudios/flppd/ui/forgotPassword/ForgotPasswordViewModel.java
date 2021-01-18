package com.antigravitystudios.flppd.ui.forgotPassword;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import com.antigravitystudios.flppd.PhonenumberKeyListener;
import com.antigravitystudios.flppd.events.RecoverEvent;
import com.antigravitystudios.flppd.models.Message;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ForgotPasswordViewModel extends BaseActivityViewModel<ForgotPasswordActivity> {

    public ObservableField<String> phonenumber = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean();

    public View.OnKeyListener phoneKeyListener;

    public ForgotPasswordViewModel(ForgotPasswordActivity activity) {
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

    private void attemptRecover(String phonenumber) {
        loading.set(true);
        NetworkModule.getInteractor().recover(phonenumber);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResult(RecoverEvent event) {
        if (event.isSuccessful()) {
            onResult(event.getResult());
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    public void onResult(Message message) {
        activity.showToast(message.getMessage());
        activity.finish();
    }

    public void onSendClick() {
        activity.hideKeyboard();
        attemptRecover(phonenumber.get());
    }

}
