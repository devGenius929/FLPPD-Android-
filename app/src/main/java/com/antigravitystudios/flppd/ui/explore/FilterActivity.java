package com.antigravitystudios.flppd.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityFilterBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class FilterActivity extends BaseBindingActivity<ActivityFilterBinding, FilterViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.filtering);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected FilterViewModel addViewModel() {
        return new FilterViewModel(this);
    }

    @Override
    protected ActivityFilterBinding getBinding(LayoutInflater inflater) {
        return ActivityFilterBinding.inflate(inflater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_toolbar, menu);
        return true;
    }
}
