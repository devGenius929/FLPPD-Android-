package com.antigravitystudios.flppd.ui.property;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antigravitystudios.flppd.BillingManager;
import com.antigravitystudios.flppd.PropertyImagesPagerAdapter;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.events.PropertyAddEvent;
import com.antigravitystudios.flppd.events.PropertyUpdateEvent;
import com.antigravitystudios.flppd.events.PurchaseCancelEvent;
import com.antigravitystudios.flppd.events.PurchaseUpdateEvent;
import com.antigravitystudios.flppd.models.Photo;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.models.PropertyImage;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.filestack.Client;
import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class PropertyEditViewModel extends BaseActivityViewModel<PropertyEditActivity> implements ViewPager.OnPageChangeListener {

    private Gson gson = new Gson();

    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean noImages = new ObservableBoolean(true);
    public ObservableField<String> photosText = new ObservableField<>();

    public ObservableField<String> streetError = new ObservableField<>();
    public ObservableField<String> cityError = new ObservableField<>();
    public ObservableField<String> stateError = new ObservableField<>();
    public ObservableField<String> zip_codeError = new ObservableField<>();
    public ObservableField<String> property_categoryError = new ObservableField<>();
    public ObservableField<String> nbedsError = new ObservableField<>();
    public ObservableField<String> nbathError = new ObservableField<>();
    public ObservableField<String> sqftError = new ObservableField<>();
    public ObservableField<String> year_builtError = new ObservableField<>();
    public ObservableField<String> parkingError = new ObservableField<>();
    public ObservableField<String> zoningError = new ObservableField<>();
    public ObservableField<String> lot_sizeError = new ObservableField<>();
    public ObservableField<String> descriptionError = new ObservableField<>();
    public ObservableField<String> property_typeError = new ObservableField<>();
    public ObservableField<String> priceError = new ObservableField<>();
    public ObservableField<String> arvError = new ObservableField<>();
    public ObservableField<String> rehab_costError = new ObservableField<>();

    public PropertyImagesPagerAdapter pagerAdapter;
    public Property property;
    public boolean isEdit;

    private int initListingType;
    private ViewPager viewPager;
    private Uri tempURI;
    private ArrayList<PropertyImage> propertyImages = new ArrayList<>();
    private boolean consumeAfterPost = false;

    public PropertyEditViewModel(PropertyEditActivity activity) {
        super(activity);

        property = new Property();

        viewPager = activity.binding.pager;
        pagerAdapter = new PropertyImagesPagerAdapter(viewPager);

        viewPager.addOnPageChangeListener(this);

        isEdit = false;
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    public void setProperty(Property property) {
        this.property = property;
        initListingType = property.getProperty_listing_id();
        isEdit = true;
        fillPropertyImages();
    }

    private void fillPropertyImages() {
        ArrayList<Photo> items = property.getPhotos();

        for (Photo item : items) {
            addImage(item.getImage());
        }
        viewPager.setCurrentItem(0, false);
    }

    private void updatePhotosText() {
        if (propertyImages.isEmpty()) {
            noImages.set(true);
        } else {
            photosText.set((viewPager.getCurrentItem() + 1) + " of " + propertyImages.size());
            noImages.set(false);
        }
    }

    private void addImage(String url) {
        propertyImages.add(new PropertyImage(url));
        ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.pager_item_property_image, null);
        ImageView imageView = view.findViewById(R.id.image);
        Picasso.with(activity).load(url).fit().centerCrop().into(imageView);
        pagerAdapter.addView(view);
        viewPager.setCurrentItem(pagerAdapter.getCount() - 1, true);
        updatePhotosText();
    }

    private void addImage(Uri uri) {
        propertyImages.add(new PropertyImage(uri));
        ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.pager_item_property_image, null);
        ImageView imageView = view.findViewById(R.id.image);
        Picasso.with(activity).load(uri).fit().centerCrop().into(imageView);
        pagerAdapter.addView(view);
        viewPager.setCurrentItem(pagerAdapter.getCount() - 1, true);
        updatePhotosText();
    }

    public void removeSelectedImage() {
        int position = viewPager.getCurrentItem();
        propertyImages.remove(position);
        pagerAdapter.removeView(position);
        updatePhotosText();
    }

    private String getImagesString() {
        StringBuilder result = new StringBuilder();

        for (PropertyImage item : propertyImages) {
            String url = item.getUrl();
            if (url == null) continue;
            result.append(url).append(",");
        }

        if (result.length() > 0) {
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        return result.toString();
    }

    private void uploadImagesAndCommitProperty() {
        final ArrayList<PropertyImage> propertiesToDownload = new ArrayList<>();

        for (PropertyImage item : propertyImages) {
            if (item.getUri() == null) continue;
            propertiesToDownload.add(item);
        }

        final Runnable onImageUploadCompleteCallback = new Runnable() {
            @Override
            public void run() {
                property.setPhoto_data(getImagesString());
                property.setDefault_img(propertyImages.get(0).getUrl());
                checkPurchases();
            }
        };

        if (propertiesToDownload.isEmpty()) {
            onImageUploadCompleteCallback.run();
            return;
        }

        activity.showToast("Uploading images");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Config config = new Config(activity.getString(R.string.filestack_key));
                Client client = new Client(config);

                StorageOptions options = new StorageOptions.Builder()
                        .mimeType("image/jpeg")
                        .build();

                for (PropertyImage item : propertiesToDownload) {
                    try {
                        FileLink file = client.upload(item.getUri().getPath(), false, options);
                        item.setUrl(activity.getString(R.string.filestack_domain) + file.getHandle());
                        item.setUri(null);
                    } catch (final IOException ignored) {
                    }
                }
                activity.runOnUiThread(onImageUploadCompleteCallback);
            }
        }).start();
    }

    private void checkPurchases() {
        if (initListingType != 2 && property.getProperty_listing_id() == 2) {
            consumeAfterPost = true;
            if (!BillingManager.getInstance().isPlatinumListingPurchased.get()) {
                BillingManager.launchPlatinumFlow(activity);
                return;
            }
        }
        sendData();
    }

    private void sendData() {
        if (isEdit) {
            attemptUpdateProperty();
        } else {
            attemptAddProperty();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPurchaseUpdateResult(PurchaseUpdateEvent event) {
        if (event.getPurchase().getSku().equals("platinum_25")) {
            BillingManager.setPlatinumToken(event.getPurchase().getPurchaseToken());
            sendData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPurchaseCancelResult(PurchaseCancelEvent event) {
        loading.set(false);
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    public void onImageCameraClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createProfileFile();
            if (photoFile != null) {
                tempURI = FileProvider.getUriForFile(activity, "com.antigravitystudios.flppd.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempURI);
                activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createProfileFile() {
        File image = null;
        try {
            String imageFileName = "TempProfileImg";
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void onImageGalleryClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
    }

    private void startCropActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setFixAspectRatio(true)
                .setAspectRatio(16, 9)
                .setInitialCropWindowPaddingRatio(0)
                .setRequestedSize(1280, 720, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                startCropActivity(tempURI);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                startCropActivity(data.getData());
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                addImage(result.getUri());
            }
        }
    }

    @Override
    public boolean onActivityOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                onSaveClick();
                return true;
            default:
                return false;
        }
    }

    public void onSaveClick() {
        boolean cancel = false;

        streetError.set("");
        cityError.set("");
        stateError.set("");
        zip_codeError.set("");
        property_categoryError.set("");
        nbedsError.set("");
        nbathError.set("");
        sqftError.set("");
        year_builtError.set("");
        parkingError.set("");
        zoningError.set("");
        lot_sizeError.set("");
        descriptionError.set("");
        property_typeError.set("");
        priceError.set("");
        arvError.set("");
        rehab_costError.set("");

        if (propertyImages.isEmpty()) {
            activity.showToast("Please add images");
            cancel = true;
        }

        if (!isFieldValid(property.getStreet(), streetError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getCity(), cityError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getState(), stateError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getZip_code(), zip_codeError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getProperty_category(), property_categoryError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getSqft(), sqftError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getYear_built(), year_builtError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getParking(), parkingError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getLot_size(), lot_sizeError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getDescription(), descriptionError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(String.valueOf(property.getProperty_type_id()), property_typeError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getPrice(), priceError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getArv(), arvError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getRehab_cost(), rehab_costError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getNbeds(), nbedsError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getNbath(), nbathError, "Invalid value")) {
            cancel = true;
        }
        if (!isFieldValid(property.getZoning(), zoningError, "Invalid value")) {
            cancel = true;
        }

        if (cancel) {
            activity.showToast("Please fill all fields");
            return;
        }

        loading.set(true);

        uploadImagesAndCommitProperty();
    }

    private boolean isFieldValid(String value, ObservableField<String> errorField, String errorText) {
        boolean valid = !TextUtils.isEmpty(value);

        if (!valid) {
            errorField.set(errorText);
        }

        return valid;
    }

    private void attemptUpdateProperty() {
        NetworkModule.getInteractor().updateProperty(property);
    }

    private void attemptAddProperty() {
        NetworkModule.getInteractor().addProperty(property);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyUpdateResult(PropertyUpdateEvent event) {
        if (event.isSuccessful()) {
            exit(event.getResult());
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyAddResult(PropertyAddEvent event) {
        if (event.isSuccessful()) {
            exit(null);
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    private void exit(Property property) {
        if(consumeAfterPost) {
            consumeAfterPost = false;
            BillingManager.consumePlatinum();
        }

        Intent intent = new Intent();
        if (property != null) {
            String s = gson.toJson(property);
            intent.putExtra("property", s);
        }
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updatePhotosText();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
