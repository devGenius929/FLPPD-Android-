package com.antigravitystudios.flppd.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.FragmentMessagesBinding;
import com.antigravitystudios.flppd.ui.base.BaseBindingFragment;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class MessagesFragment extends BaseBindingFragment<FragmentMessagesBinding, MessagesViewModel> {

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
    protected MessagesViewModel addViewModel() {
        return new MessagesViewModel(this);
    }

    @Override
    protected FragmentMessagesBinding getBinding(LayoutInflater inflater, ViewGroup container, boolean b) {
        return FragmentMessagesBinding.inflate(inflater, container, b);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search, menu);

        MaterialSearchView searchView = ((MainActivity) getActivity()).binding.searchView;
        MenuItem item = menu.findItem(R.id.search_item);
        item.setVisible(false);

        viewModel.setSearchView(searchView, item);
    }

}
