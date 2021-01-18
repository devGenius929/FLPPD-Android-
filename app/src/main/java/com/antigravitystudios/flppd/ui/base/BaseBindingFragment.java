package com.antigravitystudios.flppd.ui.base;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.BR;

public abstract class BaseBindingFragment<B extends ViewDataBinding, VM extends BaseFragmentViewModel> extends Fragment {

    public B binding;
    public VM viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = getBinding(getLayoutInflater(), container, false);

        if (viewModel == null) {
            viewModel = addViewModel();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.onFragmentViewCreated();
        setViewModel(viewModel);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewModel.onFragmentDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return viewModel.onFragmentOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onFragmentActivityResult(requestCode, resultCode, data);
    }

    protected abstract VM addViewModel();

    public void setViewModel(VM viewModel) {
        this.viewModel = viewModel;
        if (binding != null) {
            binding.setVariable(BR.viewModel, viewModel);
        }
    }

    protected abstract B getBinding(LayoutInflater inflater, ViewGroup container, boolean b);
}
