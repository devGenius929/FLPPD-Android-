package com.antigravitystudios.flppd.ui.Evaluate.NewFlip;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmPhoto;
import com.antigravitystudios.flppd.models.realm.RealmProperty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstStepNewFlipFragmet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstStepNewFlipFragmet extends Fragment implements ViewPager.OnPageChangeListener  {

    private RealmProperty property;
    private final static int REQUEST_CODE_FROM_GALLERY = 0;
    private final static int REQUEST_CODE_FROM_CAMERA = 1;

    private RealmList<RealmPhoto> images;
    private FragmentStatePagerAdapter adapter;

    private FirstStepNewFlipFragmet.OnNextButtonCLickedListener mListener;

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

    @BindView(R.id.bt_next)
    Button bt_next;

    @OnClick(R.id.bt_next)
    public void next(){
        if (mListener!=null){
            if (validateFields()){
                property = new RealmProperty();
                property.setStreet(et_street.getText().toString());
                property.setCity(et_city.getText().toString());
                property.setState(et_state.getText().toString());
                property.setZip_code(et_postal_code.getText().toString());
                property.setNbeds((et_bedrooms.getText().toString()));
                property.setNbath((et_bathrooms.getText().toString()));
                property.setSqft(Float.parseFloat(et_sqft.getText().toString()));
                property.setDescription(et_description.getText().toString());

                property.setPhotos(images);

                mListener.onNextButtonClicked(property);
            }

        }

    }

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
        if (getActivity()!=null){
/*            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);*/

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if ((resultCode == RESULT_OK) && (data!=null)){
            if (requestCode == REQUEST_CODE_FROM_CAMERA){
                String img = null; //data.getStringExtra(CameraActivity.IMAGE);
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

    public FirstStepNewFlipFragmet() {
        // Required empty public constructor
    }


    public static FirstStepNewFlipFragmet newInstance() {
        FirstStepNewFlipFragmet fragment = new FirstStepNewFlipFragmet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first_step_new_flip_fragmet, container, false);

        ButterKnife.bind(this,v);

        images= new RealmList<>();

        if (getActivity()!=null)
            adapter = new FirstStepNewFlipFragmet.ViewPagerAdapter(getActivity().getSupportFragmentManager(), images);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        updateLayoutControls(images.size());

        return v;
    }

    private void addImage(String image){
        images.add(new RealmPhoto(image));
        viewPager.setAdapter(adapter);
        updateLayoutControls(images.size());
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FirstStepNewFlipFragmet.OnNextButtonCLickedListener) {
            mListener = (FirstStepNewFlipFragmet.OnNextButtonCLickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNextButtonCLickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private boolean validateFields(){

        boolean validated = true;



        if (TextUtils.isEmpty(et_description.getText())){
            et_description.setError("Can't be empty");
            et_description.requestFocus();
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

            toast("Should have at least one image");

            scrollView.smoothScrollTo(0,0);

            validated = false;
        }
        return validated;

    }

    private void toast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();

    }


    public interface OnNextButtonCLickedListener {
        void onNextButtonClicked(RealmProperty property);
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
