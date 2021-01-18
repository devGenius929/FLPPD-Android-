package com.antigravitystudios.flppd.ui.property;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.WorkaroundMapFragment;
import com.antigravitystudios.flppd.databinding.ActivityPropertyBinding;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;

public class PropertyActivity extends BaseBindingActivity<ActivityPropertyBinding, PropertyViewModel> implements OnMapReadyCallback {

    private Gson gson = new Gson();
    private Property property;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        property = gson.fromJson(getIntent().getStringExtra("property"), Property.class);
        viewModel.setProperty(property);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                binding.scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    protected PropertyViewModel addViewModel() {
        return new PropertyViewModel(this);
    }

    @Override
    protected ActivityPropertyBinding getBinding(LayoutInflater inflater) {
        return ActivityPropertyBinding.inflate(inflater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.property_toolbar, menu);

        if (!property.getUser().getUserId().equals(SPreferences.getUser().getUserId())) {
            menu.findItem(R.id.edit).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setMapToolbarEnabled(false);

        viewModel.onMapReady(map);
    }
}
