package com.antigravitystudios.flppd.ui.explore;

import android.app.Activity;
import android.content.Intent;
import android.databinding.Bindable;
import android.view.MenuItem;

import com.antigravitystudios.flppd.BR;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.PropertiesFilter;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

public class FilterViewModel extends BaseActivityViewModel<FilterActivity> {

    private Gson gson = new Gson();

    @Bindable
    public PropertiesFilter getPropertiesFilter() {
        return propertiesFilter;
    }

    public PropertiesFilter propertiesFilter;

    public FilterViewModel(FilterActivity activity) {
        super(activity);

        propertiesFilter = gson.fromJson(activity.getIntent().getStringExtra("filter"), PropertiesFilter.class);
    }

    @Override
    protected void registerBus(EventBus bus) {

    }

    @Override
    protected void unregisterBus(EventBus bus) {

    }

    @Override
    public boolean onActivityOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                onSaveClick();
                return true;
            default:
                return false;
        }
    }

    public void onClearAllClick() {
        propertiesFilter = new PropertiesFilter();
        notifyPropertyChanged(BR.propertiesFilter);
    }

    public void onSaveClick() {
        Intent data = new Intent();
        data.putExtra("filter", gson.toJson(propertiesFilter));
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }
}
