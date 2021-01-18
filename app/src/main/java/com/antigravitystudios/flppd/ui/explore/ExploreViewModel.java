package com.antigravitystudios.flppd.ui.explore;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.antigravitystudios.flppd.BillingManager;
import com.antigravitystudios.flppd.FragmentTabsAdapter;
import com.antigravitystudios.flppd.PropertyItemListener;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.PropertiesListReceivedEvent;
import com.antigravitystudios.flppd.models.PropertiesFilter;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;
import com.antigravitystudios.flppd.ui.property.PropertyActivity;
import com.antigravitystudios.flppd.ui.property.PropertyEditActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ExploreViewModel extends BaseFragmentViewModel<ExploreFragment> implements PropertyItemListener {

    private Gson gson = new Gson();

    private static final int FilterRC = 1;
    private static final int RefreshOnSuccessRC = 2;

    public ObservableBoolean loading = new ObservableBoolean();
    public FragmentTabsAdapter adapter;
    public ArrayList<Property> properties = new ArrayList<>();

    private PropertiesFilter propertiesFilter = new PropertiesFilter();
    private Bundle arguments;

    ExploreListViewModel exploreListViewModel;
    ExploreMapViewModel exploreMapViewModel;

    public ExploreViewModel(ExploreFragment fragment) {
        super(fragment);

        arguments = fragment.getArguments();

        ExploreListFragment listFragment = new ExploreListFragment();
        exploreListViewModel = new ExploreListViewModel(listFragment, this);
        listFragment.setViewModel(exploreListViewModel);

        ExploreMapFragment mapFragment = new ExploreMapFragment();
        exploreMapViewModel = new ExploreMapViewModel(mapFragment, this);
        mapFragment.setViewModel(exploreMapViewModel);

        adapter = new FragmentTabsAdapter(fragment.getChildFragmentManager());
        adapter.addFragment(listFragment, activity.getString(R.string.tab_list));
        adapter.addFragment(mapFragment, activity.getString(R.string.tab_map));

        loadProperties();
    }

    @Override
    protected void onFragmentActivityResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentActivityResult(requestCode, resultCode, data);

        if (requestCode == FilterRC && resultCode == RESULT_OK) {
            propertiesFilter = gson.fromJson(data.getStringExtra("filter"), PropertiesFilter.class);
            loadProperties();
        } else if (requestCode == RefreshOnSuccessRC && resultCode == RESULT_OK) {
            loadProperties();
        }
    }

    private void setProperties(List<Property> properties) {
        this.properties.clear();
        if (!BillingManager.isSubscriptionActive.get()) {
            int k = properties.size();
            if (k > 5)
                properties.subList(5, k).clear();
        }
        this.properties.addAll(properties);
        exploreListViewModel.onPropertiesListChanged();
        exploreMapViewModel.onPropertiesListChanged();
    }

    private void loadProperties() {
        loading.set(true);

        if (arguments.getString("type", "").equals("all")) {
            NetworkModule.getInteractor().getProperties(null, null, propertiesFilter);
        } else if (arguments.getString("type", "").equals("starred")) {
            NetworkModule.getInteractor().getFavoriteProperties(SPreferences.getToken());
        } else if (arguments.getInt("userid") > 0) {
            NetworkModule.getInteractor().getProperties(arguments.getInt("userid"), null, propertiesFilter);
        }
    }

    @Override
    protected boolean onFragmentOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                fragment.startActivityForResult(new Intent(activity, PropertyEditActivity.class), RefreshOnSuccessRC);
                return true;
            case R.id.filter:
                Intent intent = new Intent(activity, FilterActivity.class);
                intent.putExtra("filter", gson.toJson(propertiesFilter));
                fragment.startActivityForResult(intent, FilterRC);
            default:
                return false;
        }
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertiesListReceivedResult(PropertiesListReceivedEvent event) {
        if (event.getType() != null && !arguments.getString("type", "").equals(event.getType()))
            return;
        if (event.getUserId() != null && arguments.getInt("userid") != event.getUserId()) return;

        loading.set(false);
        if (event.isSuccessful()) {
            setProperties(event.getResult());
        } else {
            activity.showToast(event.getErrorMessage());
        }
    }

    @Override
    public void onItemClicked(Property property) {
        if (!BillingManager.isSubscriptionActive.get()) {
            Toast.makeText(activity, "No subscription", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(activity, PropertyActivity.class);
            intent.putExtra("property", gson.toJson(property));
            fragment.startActivityForResult(intent, RefreshOnSuccessRC);
        }
    }

}
