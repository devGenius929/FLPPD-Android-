package com.antigravitystudios.flppd.ui.home;

import android.view.LayoutInflater;

import com.antigravitystudios.flppd.databinding.ActivityHomeBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class HomeActivity extends BaseBindingActivity<ActivityHomeBinding, HomeViewModel> {

    @Override
    protected HomeViewModel addViewModel() {
        return new HomeViewModel(this);
    }

    @Override
    protected ActivityHomeBinding getBinding(LayoutInflater inflater) {
        return ActivityHomeBinding.inflate(inflater);
    }
}
