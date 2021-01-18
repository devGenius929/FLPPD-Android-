package com.antigravitystudios.flppd.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.antigravitystudios.flppd.databinding.ActivityTutorialBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class TutorialActivity extends BaseBindingActivity<ActivityTutorialBinding, TutorialViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.tabDots.setupWithViewPager(binding.pager, true);
        viewModel.pager = binding.pager;
    }

    @Override
    protected TutorialViewModel addViewModel() {
        return new TutorialViewModel(this);
    }

    @Override
    protected ActivityTutorialBinding getBinding(LayoutInflater inflater) {
        return ActivityTutorialBinding.inflate(inflater);
    }

}
