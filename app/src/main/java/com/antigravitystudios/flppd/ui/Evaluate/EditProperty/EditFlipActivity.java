package com.antigravitystudios.flppd.ui.Evaluate.EditProperty;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.antigravitystudios.flppd.Presentation.Custom.Custom.CustomNumberPicker;
import com.antigravitystudios.flppd.Presentation.Utils.ImageUtils;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmPhoto;
import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.ui.Evaluate.NewFlip2.NewFlipActivity;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class EditFlipActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Realm realm;
    private RealmProperty property;

    private FragmentStatePagerAdapter adapter;

    private final static int REQUEST_CODE_FROM_GALLERY = 0;
    private final static int REQUEST_CODE_FROM_CAMERA = 1;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.layout_images)
    LinearLayout layout_images;

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_street)
    TextInputEditText et_street;

    @BindView(R.id.et_city)
    TextInputEditText et_city;

    @BindView(R.id.et_state)
    TextInputEditText et_state;

    @BindView(R.id.et_postal_code)
    TextInputEditText et_postal_code;

    @BindView(R.id.et_nickname)
    TextInputEditText et_nickname;

    @BindView(R.id.et_property_type)
    TextInputEditText et_property_type;

    @BindView(R.id.et_bedrooms)
    TextInputEditText et_bedrooms;

    @BindView(R.id.et_bathrooms)
    TextInputEditText et_bathrooms;

    @BindView(R.id.et_sqft)
    TextInputEditText et_sqft;

    @BindView(R.id.et_year_built)
    TextInputEditText et_year_built;

    @BindView(R.id.et_parking)
    TextInputEditText et_parking;

    @BindView(R.id.et_lot_size)
    TextInputEditText et_lot_size;

    @BindView(R.id.et_zoning)
    TextInputEditText et_zoning;

    @BindView(R.id.et_description)
    EditText et_description;

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
/*        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);*/

    }

    @OnClick(R.id.et_property_type)
    public void clickPropertyType(View view){
        showPropertyTypeDialog();
    }

    @OnClick(R.id.et_bedrooms)
    public void clickBedrooms(View view){
        showBedroomsDialog();
    }

    @OnClick(R.id.et_bathrooms)
    public void clickBathrooms(View view){
        showBathroomsDialog();
    }

    @OnClick(R.id.et_sqft)
    public void clickSqft(View view){
        showSqftDialog();
    }

    @OnClick(R.id.et_year_built)
    public void clickYearBuilt(View view){
        showYearBuiltDialog();
    }

    @OnClick(R.id.et_parking)
    public void clickParking(View view){
        showParkingDialog();
    }

    @OnClick(R.id.et_lot_size)
    public void clickLotSize(View view){
        showLotSizeDialog();
    }

    @OnClick(R.id.et_zoning)
    public void clickZoning(View view){
        showZoningDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flip);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        property =  realm.where(RealmProperty.class).equalTo("id",getIntent().getStringExtra("id")).findFirst();
        initElements();
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



    private void initElements(){


        et_property_type.setInputType(InputType.TYPE_NULL);
        et_property_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showPropertyTypeDialog();
                } else {
                }
            }
        });

        et_bathrooms.setInputType(InputType.TYPE_NULL);
        et_bathrooms.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showBathroomsDialog();
                } else {
                }
            }
        });

        et_bedrooms.setInputType(InputType.TYPE_NULL);
        et_bedrooms.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showBedroomsDialog();
                } else {
                }
            }
        });
        et_year_built.setInputType(InputType.TYPE_NULL);
        et_year_built.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showYearBuiltDialog();
                } else {
                }
            }
        });
        et_parking.setInputType(InputType.TYPE_NULL);
        et_parking.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showParkingDialog();
                } else {
                }
            }
        });
        et_zoning.setInputType(InputType.TYPE_NULL);
        et_zoning.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showZoningDialog();
                } else {
                }
            }
        });

        et_sqft.setInputType(InputType.TYPE_NULL);
        et_sqft.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showSqftDialog();
                } else {
                }
            }
        });

        et_lot_size.setInputType(InputType.TYPE_NULL);
        et_lot_size.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showLotSizeDialog();
                } else {
                }
            }
        });
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
        if (!property.getNickname().isEmpty()) et_nickname.setText(property.getNickname());
        et_street.setText(property.getStreet());
        et_city.setText(property.getCity());
        et_state.setText(property.getState());
        et_postal_code.setText(property.getZip_code());

        et_property_type.setText(property.getProperty_type().toString());
        et_bedrooms.setText(property.getNbeds().toString());
        et_bathrooms.setText(property.getNbath().toString());



        et_sqft.setText(property.getSqft().toString()+" sqft");
        et_year_built.setText(String.valueOf(property.getYear_built()));
        et_parking.setText(property.getParking().toString());
        et_lot_size.setText(property.getLot_size().toString()+" sqft");
        et_zoning.setText(property.isZoning()?"Yes":"No");

        et_description.setText(property.getDescription());


    }

    private void storeData(){
        if (!et_nickname.getText().toString().isEmpty()) property.setNickname(et_nickname.getText().toString());
        if (!et_street.getText().toString().isEmpty()) property.setStreet(et_street.getText().toString());
        if (!et_city.getText().toString().isEmpty()) property.setCity(et_city.getText().toString());
        if (!et_state.getText().toString().isEmpty()) property.setState(et_state.getText().toString());
        if (!et_postal_code.getText().toString().isEmpty()) property.setZip_code(et_postal_code.getText().toString());
        if (!et_bedrooms.getText().toString().isEmpty()) property.setNbeds((et_bedrooms.getText().toString()));
        if (!et_bathrooms.getText().toString().isEmpty()) property.setNbath((et_bathrooms.getText().toString()));
        if (!et_sqft.getText().toString().isEmpty()) property.setSqft(Float.valueOf(et_sqft.getText().toString().replaceAll(" sqft","")));

        if (!et_year_built.getText().toString().isEmpty()) property.setYear_built(Integer.parseInt(et_year_built.getText().toString()));
        if (!et_parking.getText().toString().isEmpty()) property.setParking(et_parking.getText().toString());
        if (!et_lot_size.getText().toString().isEmpty()) property.setLot_size(Float.valueOf(et_lot_size.getText().toString().replaceAll(" sqft","")));
        if (!et_zoning.getText().toString().isEmpty()) property.setZoning((et_zoning.getText().toString().equals("Yes")));

        if (!et_description.getText().toString().isEmpty()) property.setDescription(et_description.getText().toString());
    }

    private void updateLayoutControls(int selected){


        if (property.getPhotos().size() == 0) {
            layout_images.setVisibility(View.GONE);
            layout_controls.setVisibility(View.INVISIBLE);
        } else {
            layout_images.setVisibility(View.VISIBLE);
            layout_controls.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(selected);
        }
        tv_total.setText(String.valueOf(property.getPhotos().size()));
        tv_position.setText(String.valueOf(selected));
    }

    private AlertDialog showDialog(final String[] data, String title, final NewFlipActivity.itemClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(which,data[which]);
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private AlertDialog numberPickerDialog(final String[] data, String title, final NewFlipActivity.itemClickListener itemClickListener){

        final CustomNumberPicker numberPicker = new CustomNumberPicker(this);
        numberPicker.setDisplayedValues(data);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(data.length-1);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(numberPicker);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemClickListener.onClick(numberPicker.getValue(), data[numberPicker.getValue()]);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }

    private AlertDialog editTextDialog(String title, final NewFlipActivity.itemClickListener itemClickListener){

        LayoutInflater inflater = getLayoutInflater();
        View layout=inflater.inflate(R.layout.edit_text_dialog,null);

        final EditText data= layout.findViewById(R.id.et_data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
        builder.setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!data.getText().toString().isEmpty())
                    itemClickListener.onClick(0,data.getText().toString()+" sqft");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void showSqftDialog(){
        String title = "Square Footage";
        AlertDialog dialog = editTextDialog(title, new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_sqft.setText(text);
            }
        });
        dialog.show();
    }

    private void showLotSizeDialog(){
        String title = "Lot Size";
        AlertDialog dialog = editTextDialog(title, new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_lot_size.setText(text);
            }
        });
        dialog.show();
    }

    private void showBedroomsDialog(){
        String[] data = {"0","0.5","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10","10+"};
        String title = "Bedrooms";
        AlertDialog dialog = numberPickerDialog(data, title, new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_bedrooms.setText(text);
            }
        });
        dialog.show();
    }

    private void showYearBuiltDialog(){
        int initial_year = 1900;
        int total = 1+ Calendar.getInstance().get(Calendar.YEAR)-initial_year;
        String[] data = new String[total];

        for (int i = 0; i < total; i++) {
            data[i] = String.valueOf(initial_year + i);
        }
        String title = "Year Built";
        AlertDialog dialog = numberPickerDialog(data, title, new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_year_built.setText(text);
            }
        });
        dialog.show();
    }
    private void showBathroomsDialog(){
        final String[] data = {"0","0.5","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10","10+"};
        String title = "Bathrooms";
        AlertDialog dialog = numberPickerDialog(data, title, new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_bathrooms.setText(text);
            }
        });
        dialog.show();
    }

    private void showPropertyTypeDialog(){
        final String[] data = {"Single Family","Multi Family","Townhouse/Condo","Lot","Country Homes/Acreage","Mid/High-Rise Condo"};
        AlertDialog dialog = showDialog(data, "Property Type", new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_property_type.setText(text);
            }
        });

        dialog.show();
    }

    private void showZoningDialog(){
        final String[] data = {"Yes","No"};
        AlertDialog dialog = numberPickerDialog(data, "Zoning", new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_zoning.setText(text);
            }
        });

        dialog.show();
    }

    private void showParkingDialog(){
        final String[] data = {"Attached Garage","Detached Garage","Oversized Garage","Tandem","Parking Lot"," No Parking"};
        AlertDialog dialog = numberPickerDialog(data, "Parking", new NewFlipActivity.itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_parking.setText(text);
            }
        });

        dialog.show();
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
    protected void onStart() {
        super.onStart();
        if (realm!=null)
            realm.beginTransaction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (realm!=null)
            storeData();
        realm.commitTransaction();
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
/*
            Fragment f = ImagePageFragment.newInstance(images.get(position).getImage(),true);
*/


            return null;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

}
