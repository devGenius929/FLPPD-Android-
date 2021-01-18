package com.antigravitystudios.flppd.ui.explore;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.databinding.FragmentExploreListBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingFragment;


public class ExploreListFragment extends BaseBindingFragment<FragmentExploreListBinding, ExploreListViewModel> {

    @Override
    protected ExploreListViewModel addViewModel() {
        return null;
    }

    @Override
    protected FragmentExploreListBinding getBinding(LayoutInflater inflater, ViewGroup container, boolean b) {
        return FragmentExploreListBinding.inflate(inflater, container, b);
    }
}
