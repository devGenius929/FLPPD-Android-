package com.antigravitystudios.flppd.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityProfileEditBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class ProfileEditActivity extends BaseBindingActivity<ActivityProfileEditBinding, ProfileEditViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected ProfileEditViewModel addViewModel() {
        return new ProfileEditViewModel(this);
    }

    @Override
    protected ActivityProfileEditBinding getBinding(LayoutInflater inflater) {
        return ActivityProfileEditBinding.inflate(inflater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_toolbar, menu);
        return true;
    }
}
