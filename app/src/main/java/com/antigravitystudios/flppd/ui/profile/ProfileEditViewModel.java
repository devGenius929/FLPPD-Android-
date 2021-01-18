package com.antigravitystudios.flppd.ui.profile;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antigravitystudios.flppd.CircleTransform;
import com.antigravitystudios.flppd.PhonenumberKeyListener;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.ProfileUpdateEvent;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.filestack.Client;
import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileEditViewModel extends BaseActivityViewModel<ProfileEditActivity> {

    public ObservableBoolean loading = new ObservableBoolean();

    public ObservableField<String> firstNameError = new ObservableField<>();
    public ObservableField<String> lastNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> phonenumberError = new ObservableField<>();

    public User user;

    public View.OnKeyListener phoneKeyListener;

    private Uri photoURI;
    private Uri resultURI;

    private ViewGroup areasView;

    public ProfileEditViewModel(ProfileEditActivity activity) {
        super(activity);

        phoneKeyListener = new PhonenumberKeyListener();

        user = SPreferences.getUser();

        areasView = activity.binding.areasContainer;

        fillAreas();
    }

    private void fillAreas() {
        String areas = user.getAreas();

        areasView.removeAllViews();

        if (!TextUtils.isEmpty(areas)) {
            for (final String s : areas.split(",")) {
                final ViewGroup areaView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.list_item_area_removable, areasView, false);
                areaView.setTag(s);
                ((TextView) areaView.findViewById(R.id.text1)).setText(s);
                areaView.findViewById(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        areasView.removeView(areaView.findViewWithTag(s));
                    }
                });
                areasView.addView(areaView);
            }
        }
    }

    private void addArea(final String s) {
        final ViewGroup areaView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.list_item_area_removable, areasView, false);
        areaView.setTag(s);
        ((TextView) areaView.findViewById(R.id.text1)).setText(s);
        areaView.findViewById(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areasView.removeView(areaView.findViewWithTag(s));
            }
        });
        areasView.addView(areaView);
    }

    private String getAreasString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < areasView.getChildCount(); i++) {
            View child = areasView.getChildAt(i);
            if (child instanceof ViewGroup) {
                result.append(child.getTag()).append(",");
            }
        }
        if (result.length() > 0) {
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        return result.toString();
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    public void onImageCameraClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createProfileFile();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(activity, "com.antigravitystudios.flppd.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void onImageGalleryClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                startCropActivity(photoURI);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                startCropActivity(data.getData());
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                resultURI = result.getUri();
                Picasso.with(activity).load(resultURI).fit().centerCrop().transform(new CircleTransform()).into(activity.binding.avatar);
            }
        }
    }

    private void startCropActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .setInitialCropWindowPaddingRatio(0)
                .setRequestedSize(500, 500, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(activity);
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
        phonenumberError.set("");
        emailError.set("");
        lastNameError.set("");
        firstNameError.set("");

        if (!isFirstNameValid(user.getFirstName())) {
            firstNameError.set(activity.getString(R.string.invalid_first_name));
            cancel = true;
        }

        if (!isLastNameValid(user.getLastName())) {
            lastNameError.set(activity.getString(R.string.invalid_last_name));
            cancel = true;
        }

        if (!isEmailValid(user.getEmail())) {
            emailError.set(activity.getString(R.string.invalid_email));
            cancel = true;
        }

        if (!isPhonenumberValid(user.getPhoneNumber())) {
            phonenumberError.set(activity.getString(R.string.invalid_phonenumber));
            cancel = true;
        }

        if (cancel) return;

        loading.set(true);

        user.setAreas(getAreasString());

        if (resultURI != null) {
            new Thread() {
                @Override
                public void run() {
                    uploadImage(resultURI.getPath(), new Runnable() {
                        @Override
                        public void run() {
                            resultURI = null;
                            attemptUpdateProfile(user);
                        }
                    });
                }
            }.start();
        } else {
            attemptUpdateProfile(user);
        }
    }

    private void uploadImage(String path, Runnable callback) {
        Config config = new Config(activity.getString(R.string.filestack_key));
        Client client = new Client(config);

        StorageOptions options = new StorageOptions.Builder()
                .mimeType("image/jpeg")
                .build();

        try {
            FileLink file = client.upload(path, false, options);
            user.setAvatar(activity.getString(R.string.filestack_domain) + file.getHandle());
            activity.runOnUiThread(callback);
        } catch (final IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.showToast(e.getMessage());
                    loading.set(false);
                }
            });
        }
    }

    private void attemptUpdateProfile(User user) {
        NetworkModule.getInteractor().updateUserProfile(user);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileUpdateResult(ProfileUpdateEvent event) {
        if (event.isSuccessful()) {
            activity.setResult(RESULT_OK);
            activity.finish();
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
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

    public void onAreaAddClick() {
        String area = activity.binding.newAreaField.getText().toString().trim();
        if (!area.isEmpty()) {
            activity.binding.newAreaField.setText("");
            addArea(area);
        }
    }

    private boolean isEmailValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isFirstNameValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isLastNameValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isPhonenumberValid(String text) {
        return !TextUtils.isEmpty(text);
    }

}
