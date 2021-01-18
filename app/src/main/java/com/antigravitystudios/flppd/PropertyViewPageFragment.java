package com.antigravitystudios.flppd;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.databinding.ListItemPropertyBinding;
import com.antigravitystudios.flppd.models.Property;
import com.google.gson.Gson;

public class PropertyViewPageFragment extends Fragment {

    private static Gson gson = new Gson();

    private PropertyItemListener propertyItemListener;

    public Property item;

    public static PropertyViewPageFragment newInstance(Property item) {
        Bundle bundle = new Bundle();
        bundle.putString("item", gson.toJson(item));

        PropertyViewPageFragment fragment = new PropertyViewPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item = gson.fromJson(getArguments().getString("item"), Property.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListItemPropertyBinding binding = ListItemPropertyBinding.inflate(inflater);

        binding.setItem(item);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                propertyItemListener.onItemClicked(item);
            }
        });

        return binding.getRoot();
    }

    public void setOnItemClickListener(PropertyItemListener propertyItemListener) {
        this.propertyItemListener = propertyItemListener;
    }

}
