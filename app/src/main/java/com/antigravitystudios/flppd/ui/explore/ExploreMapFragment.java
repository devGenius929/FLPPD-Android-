package com.antigravitystudios.flppd.ui.explore;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.Utils;
import com.antigravitystudios.flppd.databinding.FragmentExploreMapBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class ExploreMapFragment extends BaseBindingFragment<FragmentExploreMapBinding, ExploreMapViewModel> implements OnMapReadyCallback {

    @Override
    protected ExploreMapViewModel addViewModel() {
        return null;
    }

    @Override
    protected FragmentExploreMapBinding getBinding(LayoutInflater inflater, ViewGroup container, boolean b) {
        return FragmentExploreMapBinding.inflate(inflater, container, b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.propertiesPager.setPageMargin(Utils.convertDpToPixel(12, getActivity()));

        viewModel.setPropertiesPager(binding.propertiesPager);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setMapToolbarEnabled(false);

        viewModel.onMapReady(map);
    }

}
