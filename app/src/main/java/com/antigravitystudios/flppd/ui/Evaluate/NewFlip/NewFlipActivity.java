package com.antigravitystudios.flppd.ui.Evaluate.NewFlip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewFlipActivity extends AppCompatActivity implements NewFlipContract.View, FirstStepNewFlipFragmet.OnNextButtonCLickedListener, SecondStepNewFlipFragment.OnListPropertyListener {

    NewFlipContract.Presenter mPresenter;

    @BindView(R.id.pager)
    ViewPager mPager;

    private RealmProperty property;
    private ProgressDialog progress;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flip);

        ButterKnife.bind(this);

        initDialog();
        mPresenter = new NewFlipPresenter(this,getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New FLPPD Listing");

        mPagerAdapter = new NewFlipActivity.ScreenSlidePagerAdapter(getSupportFragmentManager(),2);

        mPager.setAdapter(mPagerAdapter);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        property = new RealmProperty();


    }

    private void initDialog(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Storing files");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
            this.finish();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (mPager.getCurrentItem()>0)
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1,false);
                else
                    this.finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListPropertyClick(RealmProperty property) {
        this.property.setProperty_type_id(property.getProperty_type_id());
        //this.property.setPrice(property.getPrice());
        //this.property.setArv(property.getArv());
        mPresenter.storeProperty(this.property);
    }

    @Override
    public void onNextButtonClicked(RealmProperty property) {
        this.property.setStreet(property.getStreet());
        this.property.setCity(property.getCity());
        this.property.setState(property.getState());
        this.property.setZip_code(property.getZip_code());
        this.property.setNbeds(property.getNbeds());
        this.property.setNbath(property.getNbath());
        this.property.setSqft(property.getSqft());
        this.property.setDescription(property.getDescription());
        this.property.setPhotos(property.getPhotos());
        mPager.setCurrentItem(1,false);
    }

    @Override
    public void showLoading() {
        progress.show();
    }

    @Override
    public void hideLoading() {
        progress.dismiss();
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onSuccess(int code, Object response) {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","ok");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }




    @Override
    public void onError(int code, String message) {
        Toast.makeText(getApplicationContext(),"Error "+code,Toast.LENGTH_SHORT).show();
    }



    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private int number_of_pages;

        public ScreenSlidePagerAdapter(FragmentManager fm, int number_of_pages) {
            super(fm);
            this.number_of_pages = number_of_pages;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = null;

            switch (position){
                case 0: f = FirstStepNewFlipFragmet.newInstance();
                    break;
                case 1: f = SecondStepNewFlipFragment.newInstance();
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return number_of_pages;
        }



    }


}
