package com.antigravitystudios.flppd.ui.forgotPassword;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityForgotPasswordBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class ForgotPasswordActivity extends BaseBindingActivity<ActivityForgotPasswordBinding, ForgotPasswordViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    viewModel.onSendClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected ForgotPasswordViewModel addViewModel() {
        return new ForgotPasswordViewModel(this);
    }

    @Override
    protected ActivityForgotPasswordBinding getBinding(LayoutInflater inflater) {
        return ActivityForgotPasswordBinding.inflate(inflater);
    }

}