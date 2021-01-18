package com.antigravitystudios.flppd.ui.explore;

import android.support.v7.widget.RecyclerView;

import com.antigravitystudios.flppd.PropertyListAdapter;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;

import org.greenrobot.eventbus.EventBus;

public class ExploreListViewModel extends BaseFragmentViewModel<ExploreListFragment> {

    private ExploreViewModel exploreViewModel;

    public RecyclerView.Adapter listAdapter;

    public ExploreListViewModel(ExploreListFragment fragment, ExploreViewModel exploreViewModel) {
        super(fragment);

        this.exploreViewModel = exploreViewModel;

        listAdapter = new PropertyListAdapter(exploreViewModel.properties, exploreViewModel);
    }

    @Override
    protected void registerBus(EventBus bus) {

    }

    @Override
    protected void unregisterBus(EventBus bus) {

    }

    public void onPropertiesListChanged() {
        listAdapter.notifyDataSetChanged();
    }
}
