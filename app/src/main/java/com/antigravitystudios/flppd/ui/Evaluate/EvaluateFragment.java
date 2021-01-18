package com.antigravitystudios.flppd.ui.Evaluate;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.ui.Evaluate.NewFlip2.NewFlipActivity;
import com.antigravitystudios.flppd.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EvaluateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EvaluateFragment extends Fragment {

    @BindView(R.id.pager)
    ViewPager pager;

    private AppBarLayout appBar;
    private TabLayout tabs;



    public EvaluateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.evaluate_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_property:
                Intent i = new Intent(getActivity(),NewFlipActivity.class);
                i.putExtra("property_type_id",pager.getCurrentItem()+1);
                startActivityForResult(i,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static EvaluateFragment newInstance() {
        EvaluateFragment fragment = new EvaluateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_evaluate, container, false);

        if (savedInstanceState == null)  {
            ButterKnife.bind(this, v);
            insertTabs(container);
            fillViewPager(pager);
            tabs.setupWithViewPager(pager);
        }

        return v;
    }

    private void insertTabs(ViewGroup container) {
        View parent = (View) container.getParent();
        appBar = parent.findViewById(R.id.appbar);
        tabs = new TabLayout(getActivity());
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        appBar.addView(tabs);
    }

    public void fillViewPager(ViewPager pager) {
        EvaluateFragment.SectionsAdapter adapter = new EvaluateFragment.SectionsAdapter(getFragmentManager());
        adapter.addFragment(EvaluateListFragment.newInstance(true),"Rental");
        adapter.addFragment(EvaluateListFragment.newInstance(false), "Flip");
        pager.setAdapter(adapter);


    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (appBar!=null)
            appBar.removeView(tabs);
    }

    public class SectionsAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titleFragments = new ArrayList<>();

        public SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            fragments.add(fragment);
            titleFragments.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleFragments.get(position);
        }
    }

}
