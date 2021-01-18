package com.antigravitystudios.flppd.ui.signup;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.antigravitystudios.flppd.databinding.ActivityRequestPhonenumberBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class RequestPhonenumberActivity extends BaseBindingActivity<ActivityRequestPhonenumberBinding, RequestPhonenumberViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.lastInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    viewModel.onNextClick();
                    return true;
                }
                return false;
            }
        });

        viewModel.setUserJson(getIntent().getStringExtra("user"));
    }

    @Override
    protected RequestPhonenumberViewModel addViewModel() {
        return new RequestPhonenumberViewModel(this);
    }

    @Override
    protected ActivityRequestPhonenumberBinding getBinding(LayoutInflater inflater) {
        return ActivityRequestPhonenumberBinding.inflate(inflater);
    }
}

