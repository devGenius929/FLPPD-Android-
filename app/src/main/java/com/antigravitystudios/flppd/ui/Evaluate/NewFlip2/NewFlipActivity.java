package com.antigravitystudios.flppd.ui.Evaluate.NewFlip2;

import android.app.Activity;
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
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.antigravitystudios.flppd.Presentation.Custom.Custom.CustomNumberPicker;
import com.antigravitystudios.flppd.Presentation.Utils.ImageUtils;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmPhoto;
import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.models.realm.RealmWorksheetFlip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class NewFlipActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {


    private Realm realm;
    RealmResults<RealmProperty> results;
    private RealmProperty selectedFlip;
    private List<String> flips = new ArrayList<>();
    private RealmList<RealmPhoto> images;
    private FragmentStatePagerAdapter adapter;

    private int property_type_id;

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

    @BindView(R.id.copy_switch)
    SwitchCompat copy_switch;

    @BindView(R.id.spinner)
    AppCompatSpinner spinner;

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
        images.remove(item);
        viewPager.setAdapter(adapter);
        updateLayoutControls(images.size());
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
        setContentView(R.layout.activity_new_flip2);
        ButterKnife.bind(this);
        images= new RealmList<>();
        property_type_id = getIntent().getIntExtra("property_type_id",1);

        realm = Realm.getDefaultInstance();
        initToolbar();
        fillSpinners();
        initElements();



        adapter = new ViewPagerAdapter(getSupportFragmentManager(), images);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        updateLayoutControls(images.size());

    }

    private void saveProperty(){
        if (validateFields()) {
            final RealmProperty flip = new RealmProperty();
            flip.setNickname(et_nickname.getText().toString());
            flip.setStreet(et_street.getText().toString());
            flip.setCity(et_city.getText().toString());
            flip.setState(et_state.getText().toString());
            flip.setZip_code(et_postal_code.getText().toString());
            flip.setProperty_type(et_property_type.getText().toString());
            flip.setNbeds((et_bedrooms.getText().toString()));
            flip.setNbath((et_bathrooms.getText().toString()));
            flip.setSqft(Float.valueOf(et_sqft.getText().toString().replaceAll(" sqft","")));
            flip.setYear_built(Integer.valueOf(et_year_built.getText().toString()));
            flip.setParking(et_parking.getText().toString());
            flip.setLot_size(Float.valueOf(et_lot_size.getText().toString().replaceAll(" sqft","")));
            flip.setZoning((et_lot_size.getText().toString().equals("Yes")));
            flip.setDescription(et_description.getText().toString());
            flip.setProperty_type_id(property_type_id);
            RealmList<RealmPhoto> photos = new RealmList<>();
            for (int i = 0; i < images.size(); i++) {
                try {
                    String uri = ImageUtils.storeImageInternal(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(images.get(i).getImage())));
                    photos.add(new RealmPhoto(uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            flip.setPhotos(photos);

            if (property_type_id==2) {
                flip.setRealmWorksheetFlip(RealmWorksheetFlip.create());

            }

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    bgRealm.insertOrUpdate(flip);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","ok");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(NewFlipActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });



        }

    }

    private boolean validateFields(){

        boolean validated = true;

        if (TextUtils.isEmpty(et_description.getText())){
            et_description.setError("Can't be empty");
            et_description.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_zoning.getText())){
            et_zoning.setError("Can't be empty");
            et_zoning.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_lot_size.getText())){
            et_lot_size.setError("Can't be empty");
            et_lot_size.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_parking.getText())){
            et_parking.setError("Can't be empty");
            et_parking.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_year_built.getText())){
            et_year_built.setError("Can't be empty");
            et_year_built.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_sqft.getText())){
            et_sqft.setError("Can't be empty");
            et_sqft.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_bathrooms.getText())){
            et_bathrooms.setError("Can't be empty");
            et_bathrooms.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_bedrooms.getText())){
            et_bedrooms.setError("Can't be empty");
            et_bedrooms.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_property_type.getText())){
            et_property_type.setError("Can't be empty");
            et_property_type.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_postal_code.getText())){
            et_postal_code.setError("Can't be empty");
            et_postal_code.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_state.getText())){
            et_state.setError("Can't be empty");
            et_state.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_city.getText())){
            et_city.setError("Can't be empty");
            et_city.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_street.getText())){
            et_street.setError("Can't be empty");
            et_street.requestFocus();
            validated = false;
        }



        if (images.size()==0){
            viewPager.requestFocus();

            Toast.makeText(this, "Should have at least one image", Toast.LENGTH_SHORT).show();

            scrollView.smoothScrollTo(0,0);

            validated = false;
        }
        return validated;

    }

    private void updateLayoutControls(int selected){


        if (images.size() == 0) {
            layout_images.setVisibility(View.GONE);
            layout_controls.setVisibility(View.INVISIBLE);
        } else {
            layout_images.setVisibility(View.VISIBLE);
            layout_controls.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(selected);
        }
        tv_total.setText(String.valueOf(images.size()));
        tv_position.setText(String.valueOf(selected));
    }

    private void addImage(String image){
        images.add(new RealmPhoto(image));
        viewPager.setAdapter(adapter);
        updateLayoutControls(images.size());
    }

    private void initElements(){

        copy_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked){
                        spinner.setVisibility(View.VISIBLE);
                        if (results.size()>0){
                            fillViewsBasedOnSelectedFlip();
                        }
                    } else {
                        spinner.setVisibility(View.GONE);
                    }


            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFlip = results.get(position);
                fillViewsBasedOnSelectedFlip();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New "+((property_type_id==1)?"Rental":"Flip"));
    }

    private void fillSpinners(){

        results =  realm.where(RealmProperty.class).findAll();

        for (RealmProperty p:results) {
            flips.add(p.getZip_code()+" "+p.getStreet()+" "+p.getCity()+" "+p.getState());
        }

        if (results.size()>0) selectedFlip = results.get(0);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_right, flips);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


    }

    private AlertDialog showDialog(final String[] data, String title, final itemClickListener listener){

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

    private AlertDialog numberPickerDialog(final String[] data, String title, final itemClickListener itemClickListener){

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

    private AlertDialog editTextDialog(String title, final itemClickListener itemClickListener){

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
        AlertDialog dialog = editTextDialog(title, new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_sqft.setText(text);
            }
        });
        dialog.show();
    }

    private void showLotSizeDialog(){
        String title = "Lot Size";
        AlertDialog dialog = editTextDialog(title, new itemClickListener() {
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
        AlertDialog dialog = numberPickerDialog(data, title, new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_bedrooms.setText(text);
            }
        });
        dialog.show();
    }

    private void showYearBuiltDialog(){
        int initial_year = 1900;
        int total = 1+Calendar.getInstance().get(Calendar.YEAR)-initial_year;
        String[] data = new String[total];

        for (int i = 0; i < total; i++) {
            data[i] = String.valueOf(initial_year + i);
        }
        String title = "Year Built";
        AlertDialog dialog = numberPickerDialog(data, title, new itemClickListener() {
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
        AlertDialog dialog = numberPickerDialog(data, title, new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_bathrooms.setText(text);
            }
        });
        dialog.show();
    }

    private void showPropertyTypeDialog(){
        final String[] data = {"Single Family","Multi Family","Townhouse/Condo","Lot","Country Homes/Acreage","Mid/High-Rise Condo"};
        AlertDialog dialog = showDialog(data, "Property Type", new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_property_type.setText(text);
            }
        });

        dialog.show();
    }

    private void showZoningDialog(){
        final String[] data = {"Yes","No"};
        AlertDialog dialog = numberPickerDialog(data, "Zoning", new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_zoning.setText(text);
            }
        });

        dialog.show();
    }

    private void showParkingDialog(){
        final String[] data = {"Attached Garage","Detached Garage","Oversized Garage","Tandem","Parking Lot"," No Parking"};
        AlertDialog dialog = numberPickerDialog(data, "Parking", new itemClickListener() {
            @Override
            public void onClick(int position, String text) {
                et_parking.setText(text);
            }
        });

        dialog.show();
    }



    private void fillViewsBasedOnSelectedFlip(){
        if (!selectedFlip.getNickname().isEmpty()) et_nickname.setText(selectedFlip.getNickname());
        et_street.setText(selectedFlip.getStreet());
        et_city.setText(selectedFlip.getCity());
        et_state.setText(selectedFlip.getState());
        et_postal_code.setText(selectedFlip.getZip_code());

        et_property_type.setText(selectedFlip.getProperty_type().toString());
        et_bedrooms.setText(selectedFlip.getNbeds().toString());
        et_bathrooms.setText(selectedFlip.getNbath().toString());



        et_sqft.setText(selectedFlip.getSqft().toString()+" sqft");
        et_year_built.setText(String.valueOf(selectedFlip.getYear_built()));
        et_parking.setText(selectedFlip.getParking().toString());
        et_lot_size.setText(selectedFlip.getLot_size().toString()+" sqft");
        et_zoning.setText(selectedFlip.isZoning()?"Yes":"No");

        et_description.setText(selectedFlip.getDescription());

        images.clear();
        images.addAll(selectedFlip.getPhotos()) ;
        viewPager.setAdapter(adapter);
        updateLayoutControls(images.size());


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
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                saveProperty();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_flip, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if ((resultCode == RESULT_OK) && (data!=null)){
            if (requestCode == REQUEST_CODE_FROM_CAMERA){
                String img = null;//data.getStringExtra(CameraActivity.IMAGE);
                if (img!=null)
                    addImage(img);
            }

            if (requestCode == REQUEST_CODE_FROM_GALLERY){
                Uri uri = data.getData();
                String img = String.valueOf(uri);
                if (img!=null){
                    addImage(img);

                }


            }


        }
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

    public interface itemClickListener {
        void onClick(int position, String text);
    }




    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private RealmList<RealmPhoto> images;

        public ViewPagerAdapter(FragmentManager fm, RealmList<RealmPhoto> imagesList) {
            super(fm);

            this.images = imagesList;
        }

        @Override
        public Fragment getItem(int position) {
/*            Fragment f = ImagePageFragment.newInstance(images.get(position).getImage(),true);*/


            return null;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

}
