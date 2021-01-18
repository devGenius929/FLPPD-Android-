package com.antigravitystudios.flppd.ui.Evaluate.EditProperty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antigravitystudios.flppd.Presentation.Utils.ImageUtils;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmPhoto;
import com.antigravitystudios.flppd.models.realm.RealmProperty;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class EditPropertyActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final static int REQUEST_CODE_FROM_GALLERY = 0;
    private final static int REQUEST_CODE_FROM_CAMERA = 1;

    private RealmProperty property;
    private Realm realm;

    private FragmentStatePagerAdapter adapter;

    @BindView(R.id.iv_delete)
    ImageView iv_delete;
    @BindView(R.id.layout_controls)
    LinearLayout layout_controls;
    @BindView(R.id.layout_from_file)
    LinearLayout layout_from_file;
    @BindView(R.id.layout_from_camera)
    LinearLayout layout_from_camera;
    @BindView(R.id.tv_position)
    TextView tv_position;
    @BindView(R.id.tv_total)
    TextView tv_total;
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    @BindView(R.id.et_street)
    EditText et_street;
    @BindView(R.id.et_city)
    EditText et_city;
    @BindView(R.id.et_state)
    EditText et_state;
    @BindView(R.id.et_postal_code)
    EditText et_postal_code;
    @BindView(R.id.et_bedrooms)
    EditText et_bedrooms;
    @BindView(R.id.et_bathrooms)
    EditText et_bathrooms;
    @BindView(R.id.et_sqft)
    EditText et_sqft;
    @BindView(R.id.et_description)
    EditText et_description;
    @BindView(R.id.et_price)
    EditText et_price;
    @BindView(R.id.et_arv)
    EditText et_arv;

    @OnClick(R.id.iv_delete)
    public void deleteImage(){
        int item = viewPager.getCurrentItem();
        property.getPhotos().remove(item);
        viewPager.setAdapter(adapter);
        updateLayoutControls(property.getPhotos().size());
    }

    @OnClick(R.id.layout_from_file)
    public void clickFromFile(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_FROM_GALLERY);

    }

    @OnClick(R.id.layout_from_camera)
    public void clickFromCamera(){
/*        Intent intent = new Intent(EditPropertyActivity.this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        property =  realm.where(RealmProperty.class).equalTo("id",getIntent().getStringExtra("id")).findFirst();
        ButterKnife.bind(this);

        setValues();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle((property.getProperty_type_id()==1)?"Rental":"Flip");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), property.getPhotos());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        updateLayoutControls(property.getPhotos().size());

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if ((resultCode == RESULT_OK) && (data!=null)){
            if (requestCode == REQUEST_CODE_FROM_CAMERA){
                String img = null;//data.getStringExtra(CameraActivity.IMAGE);
                if (img!=null)
                    try {
                        addImage(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            if (requestCode == REQUEST_CODE_FROM_GALLERY){
                Uri uri = data.getData();
                String img = String.valueOf(uri);
                if (img!=null){
                    try {
                        addImage(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }


        }
    }

    private void setValues(){
        et_street.setText(property.getStreet());
        et_city.setText(property.getCity());
        et_state.setText(property.getState());
        et_postal_code.setText(property.getZip_code());
        et_bedrooms.setText(property.getNbeds().toString());
        et_bathrooms.setText(property.getNbath().toString());
        et_sqft.setText(property.getSqft().toString());
        et_description.setText(property.getDescription());
        //et_price.setText(property.getPrice().toString());
        //et_arv.setText(property.getArv().toString());
    }

    private void storeData(){
        if (!et_street.getText().toString().isEmpty()) property.setStreet(et_street.getText().toString());
        if (!et_city.getText().toString().isEmpty()) property.setCity(et_city.getText().toString());
        if (!et_state.getText().toString().isEmpty()) property.setState(et_state.getText().toString());
        if (!et_postal_code.getText().toString().isEmpty()) property.setZip_code(et_postal_code.getText().toString());
        if (!et_bedrooms.getText().toString().isEmpty()) property.setNbeds((et_bedrooms.getText().toString()));
        if (!et_bathrooms.getText().toString().isEmpty()) property.setNbath((et_bathrooms.getText().toString()));
        if (!et_sqft.getText().toString().isEmpty()) property.setSqft(Float.valueOf(et_sqft.getText().toString()));
        if (!et_description.getText().toString().isEmpty()) property.setDescription(et_description.getText().toString());
//        /if (!et_price.getText().toString().isEmpty()) property.setPrice(Float.valueOf(et_price.getText().toString()));
        //if (!et_arv.getText().toString().isEmpty()) property.setArv(Float.valueOf(et_arv.getText().toString()));



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (realm!=null)
            storeData();
            realm.commitTransaction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (realm!=null)
            realm.beginTransaction();
    }



    private void addImage(String image) throws IOException {

        final String uri = ImageUtils.storeImageInternal(
                MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(image)));


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                property.getPhotos().add(new RealmPhoto(uri));
            }
        });

        viewPager.setAdapter(adapter);
        updateLayoutControls(property.getPhotos().size());
    }

    private void updateLayoutControls(int selected){


        if (property.getPhotos().size() == 0) {
            layout_controls.setVisibility(View.INVISIBLE);
        } else {
            layout_controls.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(selected);
        }
        tv_total.setText(String.valueOf(property.getPhotos().size()));
        tv_position.setText(String.valueOf(selected));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tv_position.setText(String.valueOf(position+1));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private RealmList<RealmPhoto> images;

        public ViewPagerAdapter(FragmentManager fm, RealmList<RealmPhoto> imagesList) {
            super(fm);

            this.images = imagesList;
        }

        @Override
        public Fragment getItem(int position) {
            /*Fragment f = ImagePageFragment.newInstance(images.get(position).getImage(),true);*/


            return null;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

}
