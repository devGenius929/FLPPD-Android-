package com.antigravitystudios.flppd.ui.tutorial;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.home.HomeActivity;

import org.greenrobot.eventbus.EventBus;

public class TutorialViewModel extends BaseActivityViewModel<TutorialActivity> {

    public ObservableBoolean lastPage = new ObservableBoolean();

    public PagerAdapter pagerAdapter;
    public ViewPager.OnPageChangeListener pagerListener;

    public ViewPager pager;

    public TutorialViewModel(TutorialActivity activity) {
        super(activity);

        pagerAdapter = new TutorialPagerAdapter(activity);
        pagerListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                lastPage.set(position == pagerAdapter.getCount() - 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @Override
    protected void registerBus(EventBus bus) {}

    @Override
    protected void unregisterBus(EventBus bus) {}

    public void onNextClick() {
        SPreferences.setTutorialShown();
        activity.startActivity(new Intent(activity, HomeActivity.class));
        activity.finish();
    }

    private class TutorialPagerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        TutorialPagerAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = inflater.inflate(R.layout.pager_item_tutorial_1, container, false);
                    break;
                case 1:
                    view = inflater.inflate(R.layout.pager_item_tutorial_2, container, false);
                    break;
                case 2:
                    view = inflater.inflate(R.layout.pager_item_tutorial_3, container, false);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.pager_item_tutorial_4, container, false);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.pager_item_tutorial_5, container, false);
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
