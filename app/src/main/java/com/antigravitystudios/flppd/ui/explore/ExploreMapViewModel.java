package com.antigravitystudios.flppd.ui.explore;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.Utils;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;
import com.antigravitystudios.flppd.PropertiesPagerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class ExploreMapViewModel extends BaseFragmentViewModel<ExploreMapFragment> implements GoogleMap.OnMarkerClickListener, ViewPager.OnPageChangeListener {

    private ExploreViewModel exploreViewModel;
    private GoogleMap map;
    private ViewPager propertiesPager;
    public PropertiesPagerAdapter propertiesPagerAdapter;
    private HashMap<Integer, Marker> markerByPropertyId = new HashMap<>();
    private boolean markersSet = false;

    public ExploreMapViewModel(ExploreMapFragment fragment, ExploreViewModel exploreViewModel) {
        super(fragment);

        this.exploreViewModel = exploreViewModel;
    }

    @Override
    protected void onFragmentViewCreated() {
        super.onFragmentViewCreated();

        propertiesPager.addOnPageChangeListener(this);
        propertiesPagerAdapter = new PropertiesPagerAdapter(fragment.getChildFragmentManager(), exploreViewModel.properties, exploreViewModel);
    }

    @Override
    protected void registerBus(EventBus bus) {

    }

    @Override
    protected void unregisterBus(EventBus bus) {

    }

    public void setPropertiesPager(ViewPager propertiesPager) {
        this.propertiesPager = propertiesPager;

    }

    public void onPropertiesListChanged() {
        propertiesPagerAdapter.notifyDataSetChanged();
        fillMapWithMarkers();
        propertiesPager.setCurrentItem(0);
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;

        map.setOnMarkerClickListener(this);

        if(!markersSet) fillMapWithMarkers();
    }

    private void fillMapWithMarkers() {
        if(map == null) return;
        markersSet = true;

        map.clear();
        markerByPropertyId.clear();

        for (Property property : exploreViewModel.properties) {
            Drawable icon = ContextCompat.getDrawable(fragment.getActivity(), R.drawable.marker_map_green);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(property.getLatitude(), property.getLongitude()))
                    .title("$" + property.getPrice_to_human())
                    .snippet("$" + property.getArv_to_human())
                    .infoWindowAnchor(0.5F, 0.5F)
                    .icon(Utils.getBitmapDescriptor(icon)));

            marker.setTag(property.getId());

            markerByPropertyId.put(property.getId(), marker);
        }
        if (exploreViewModel.properties.size() > 0)
            onPageSelected(0);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setPageByPropertyId((Integer) marker.getTag());
        return false;
    }

    private void setPageByPropertyId(Integer id) {
        for (int i = 0; i < exploreViewModel.properties.size(); i++) {
            if (exploreViewModel.properties.get(i).getId().equals(id))
                propertiesPager.setCurrentItem(i);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Marker marker = markerByPropertyId.get(exploreViewModel.properties.get(position).getId());
        marker.showInfoWindow();
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
