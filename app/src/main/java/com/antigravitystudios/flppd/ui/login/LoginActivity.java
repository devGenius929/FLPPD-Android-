package com.antigravitystudios.flppd.ui.login;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityLoginBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

public class LoginActivity extends BaseBindingActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Link forgotLink = new Link(getString(R.string.don_t_have_account))
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setUnderlined(false)
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        viewModel.onDontHaveAccountClick();
                    }
                });

        LinkBuilder.on(binding.textForgotLink)
                .setText(String.format("%s %s", getString(R.string.please_enter_your_email_address_and_password), getString(R.string.don_t_have_account)))
                .addLink(forgotLink)
                .build();

        binding.lastInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    viewModel.onSignInClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected LoginViewModel addViewModel() {
        return new LoginViewModel(this);
    }

    @Override
    protected ActivityLoginBinding getBinding(LayoutInflater inflater) {
        return ActivityLoginBinding.inflate(inflater);
    }
}

