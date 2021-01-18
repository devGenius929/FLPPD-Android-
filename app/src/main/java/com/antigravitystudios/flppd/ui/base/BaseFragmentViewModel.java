package com.antigravitystudios.flppd.ui.base;

import android.content.Intent;
import android.view.MenuItem;

public abstract class BaseFragmentViewModel<F extends BaseBindingFragment> extends BaseViewModel {

    protected F fragment;
    protected BaseBindingActivity activity;

    public BaseFragmentViewModel(F fragment) {
        super();
        this.fragment = fragment;

        activity = (BaseBindingActivity) fragment.getActivity();
    }

    protected void onFragmentDetach() {
        destroy();
    }

    protected boolean onFragmentOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected void onFragmentViewCreated() {

    }

    protected void onFragmentActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
