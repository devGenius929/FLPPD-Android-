package com.antigravitystudios.flppd.ui.explore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.databinding.FragmentExploreBinding;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.ui.base.BaseBindingFragment;


public class ExploreFragment extends BaseBindingFragment<FragmentExploreBinding, ExploreViewModel> {

    private Bundle arguments;

    public static Fragment newInstance(User user) {
        Fragment fragment = new ExploreFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userid", user.getUserId());
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(String type) {
        Fragment fragment = new ExploreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arguments = getArguments();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        binding.tabs.setupWithViewPager(binding.pager);

        return view;
    }

    @Override
    protected ExploreViewModel addViewModel() {
        return new ExploreViewModel(this);
    }

    @Override
    protected FragmentExploreBinding getBinding(LayoutInflater inflater, ViewGroup container, boolean b) {
        return FragmentExploreBinding.inflate(inflater, container, b);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.explore_toolbar, menu);

        if (arguments.getString("type", "").equals("starred")) {
            menu.findItem(R.id.add).setVisible(false);
            menu.findItem(R.id.filter).setVisible(false);
        }
    }

}
