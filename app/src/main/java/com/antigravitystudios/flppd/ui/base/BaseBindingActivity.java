package com.antigravitystudios.flppd.ui.base;

import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.antigravitystudios.flppd.BR;

public abstract class BaseBindingActivity<B extends ViewDataBinding, VM extends BaseActivityViewModel> extends AppCompatActivity {

    public B binding;
    public VM viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = getBinding(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = addViewModel();

        binding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.onActivityStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.onActivityStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onActivityDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!viewModel.onActivityBackPressed()) super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return viewModel.onActivityOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    protected abstract VM addViewModel();

    protected abstract B getBinding(LayoutInflater inflater);

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
