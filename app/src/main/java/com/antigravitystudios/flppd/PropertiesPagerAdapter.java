package com.antigravitystudios.flppd;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.antigravitystudios.flppd.models.Property;

import java.util.ArrayList;

public class PropertiesPagerAdapter extends FragmentStatePagerAdapter {

    private PropertyItemListener listener;
    private ArrayList<Property> properties;

    public PropertiesPagerAdapter(FragmentManager fm, ArrayList<Property> properties, PropertyItemListener listener) {
        super(fm);
        this.properties = properties;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        PropertyViewPageFragment fragment = PropertyViewPageFragment.newInstance(properties.get(position));
        fragment.setOnItemClickListener(listener);
        return fragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return properties.size();
    }

}
