package com.antigravitystudios.flppd.ui.network;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.FragmentNetworkBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingFragment;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class NetworkFragment extends BaseBindingFragment<FragmentNetworkBinding, NetworkViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    protected NetworkViewModel addViewModel() {
        return new NetworkViewModel(this);
    }

    @Override
    protected FragmentNetworkBinding getBinding(LayoutInflater inflater, ViewGroup container, boolean b) {
        return FragmentNetworkBinding.inflate(inflater, container, b);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search, menu);

        MaterialSearchView searchView = ((MainActivity) getActivity()).binding.searchView;
        MenuItem item = menu.findItem(R.id.search_item);

        viewModel.setSearchView(searchView, item);
    }

}
