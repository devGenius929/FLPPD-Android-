package com.antigravitystudios.flppd.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.antigravitystudios.flppd.BillingManager;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityMainBinding;
import com.antigravitystudios.flppd.databinding.NavHeaderMainBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected MainViewModel addViewModel() {
        return new MainViewModel(this);
    }

    @Override
    protected ActivityMainBinding getBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);

        viewModel.drawerLayout = binding.drawerLayout;
        viewModel.drawer = binding.drawer;
        viewModel.toolbar = getSupportActionBar();

        NavHeaderMainBinding navHeaderMainBinding = NavHeaderMainBinding.inflate(getLayoutInflater());
        navHeaderMainBinding.setViewModel(viewModel);
        View headerView = navHeaderMainBinding.getRoot();

        binding.drawer.addHeaderView(headerView);

        binding.drawer.setNavigationItemSelectedListener(this);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.onHeaderClick();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String extraType = getIntent().getStringExtra("type");
        if (extraType != null && extraType.equals("messages")) {
            onNavigationItemSelected(binding.drawer.getMenu().getItem(3));
            binding.drawer.setCheckedItem(R.id.messages);
        } else {
            onNavigationItemSelected(binding.drawer.getMenu().getItem(0));
            binding.drawer.setCheckedItem(R.id.explore);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String extraType = intent.getStringExtra("type");
        if (extraType != null && extraType.equals("messages")) {
            onNavigationItemSelected(binding.drawer.getMenu().getItem(3));
            binding.drawer.setCheckedItem(R.id.messages);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.explore:
                viewModel.onExploreClick();
                break;
            case R.id.network:
                viewModel.onNetworkClick();
                break;
            case R.id.evaluate:
                if(!BillingManager.isSubscriptionActive.get()) {
                    Toast.makeText(this, "No subscription", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.onEvaluateClick();
                }
                break;
            case R.id.messages:
                viewModel.onMessagesClick();
                break;
            case R.id.my_listings:
                viewModel.onMyListingsClick();
                break;
            case R.id.saved_listings:
                viewModel.onSavedListingsClick();
                break;
            case R.id.glossary:
                viewModel.onGlossaryClick();
                break;
            case R.id.exit:
                viewModel.onExitClick();
                break;
        }
        return true;
    }

}
