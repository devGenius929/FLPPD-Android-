package com.antigravitystudios.flppd.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivitySignUpBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class SignUpActivity extends BaseBindingActivity<ActivitySignUpBinding, SignUpViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.creating_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.tabDots.setupWithViewPager(binding.pager, true);
        viewModel.pager = binding.pager;
        viewModel.tabLayout = binding.tabDots;
    }

    @Override
    protected SignUpViewModel addViewModel() {
        return new SignUpViewModel(this);
    }

    @Override
    protected ActivitySignUpBinding getBinding(LayoutInflater inflater) {
        return ActivitySignUpBinding.inflate(inflater);
    }

}
