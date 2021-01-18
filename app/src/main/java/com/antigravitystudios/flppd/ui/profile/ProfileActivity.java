package com.antigravitystudios.flppd.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.antigravitystudios.flppd.Utils;
import com.antigravitystudios.flppd.databinding.ActivityProfileBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class ProfileActivity extends BaseBindingActivity<ActivityProfileBinding, ProfileViewModel> {

    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.propertiesPager.setPageMargin(Utils.convertDpToPixel(12, this));
    }

    @Override
    protected ProfileViewModel addViewModel() {
        userId = getIntent().getIntExtra("userId", 0);
        return new ProfileViewModel(this, userId);
    }

    @Override
    protected ActivityProfileBinding getBinding(LayoutInflater inflater) {
        return ActivityProfileBinding.inflate(inflater);
    }
}
