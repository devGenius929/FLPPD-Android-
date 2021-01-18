package com.antigravitystudios.flppd.ui.property;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.ActivityPropertyEditBinding;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;
import com.google.gson.Gson;

public class PropertyEditActivity extends BaseBindingActivity<ActivityPropertyEditBinding, PropertyEditViewModel> {

    private Gson gson = new Gson();
    private Property property;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String propertyJson = getIntent().getStringExtra("property");
        if (propertyJson != null) {
            property = gson.fromJson(propertyJson, Property.class);
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (property == null) {
            getSupportActionBar().setTitle("New FLPPD Listing");
        } else {
            getSupportActionBar().setTitle("Edit property");
            viewModel.setProperty(property);
        }
    }

    @Override
    protected PropertyEditViewModel addViewModel() {
        return new PropertyEditViewModel(this);
    }

    @Override
    protected ActivityPropertyEditBinding getBinding(LayoutInflater inflater) {
        return ActivityPropertyEditBinding.inflate(inflater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_property_toolbar, menu);

        if (property == null) {
            menu.findItem(R.id.save).setVisible(false);
        }

        return true;
    }
}
