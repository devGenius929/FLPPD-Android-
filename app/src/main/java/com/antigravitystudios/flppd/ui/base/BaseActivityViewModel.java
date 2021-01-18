package com.antigravitystudios.flppd.ui.base;

import android.content.Intent;
import android.view.MenuItem;

public abstract class BaseActivityViewModel<A extends BaseBindingActivity> extends BaseViewModel {

    protected A activity;
    protected boolean viewVisible;

    public BaseActivityViewModel(A activity) {
        super();
        this.activity = activity;
    }

    protected void onActivityDestroy() {
        destroy();
    }

    protected void onActivityStart() {
        viewVisible = true;
    }

    protected void onActivityStop() {
        viewVisible = false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}

    protected boolean onActivityBackPressed() {
        return false;
    }

    public boolean onActivityOptionsItemSelected(MenuItem item) {
        return false;
    }
}
