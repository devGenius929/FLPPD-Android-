package com.antigravitystudios.flppd.ui.property;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antigravitystudios.flppd.PropertyImagesPagerAdapter;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.Utils;
import com.antigravitystudios.flppd.events.PropertyDeletedEvent;
import com.antigravitystudios.flppd.events.PropertyReceivedEvent;
import com.antigravitystudios.flppd.models.Photo;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.profile.ProfileActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class PropertyViewModel extends BaseActivityViewModel<PropertyActivity> implements ViewPager.OnPageChangeListener {

    private static int edit_RC = 1;

    private Gson gson = new Gson();
    private GoogleMap map;

    public boolean isMe;
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean starred = new ObservableBoolean();
    public ObservableBoolean noImages = new ObservableBoolean(true);
    public ObservableField<String> photosText = new ObservableField<>();
    public Property property = new Property();

    public PropertyImagesPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    public PropertyViewModel(final PropertyActivity activity) {
        super(activity);

        viewPager = activity.binding.pager;
        pagerAdapter = new PropertyImagesPagerAdapter(viewPager);

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyDeleted(PropertyDeletedEvent event) {
        if (!property.getId().equals(event.getItemId())) return;

        if (event.isSuccessful()) {
            activity.setResult(RESULT_OK);
            activity.finish();
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyReceived(PropertyReceivedEvent event) {
        if (!property.getId().equals(event.getItemId())) return;
        loading.set(false);

        if (event.isSuccessful()) {
            property.notifyChange();
            property = event.getResult();
            isMe = property.getUser().getUserId().equals(SPreferences.getUser().getUserId());
            fillPropertyImages();
            if (property.getStarred() != null) {
                starred.set(property.getStarred());
            }
            addMarker();
        } else {
            activity.showToast(event.getErrorMessage());
        }
    }

    @Override
    public boolean onActivityOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(activity, PropertyEditActivity.class);
                intent.putExtra("property", gson.toJson(property));
                activity.startActivityForResult(intent, edit_RC);
                return true;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Delete")
                        .setMessage("Do you want to remove this property?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NetworkModule.getInteractor().deleteProperty(property);
                                loading.set(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == edit_RC && resultCode == RESULT_OK) {
            setProperty(gson.fromJson(data.getStringExtra("property"), Property.class));
        }
    }

    private void fillPropertyImages() {
        ArrayList<Photo> items = property.getPhotos();

        for (Photo item : items) {
            addImage(item.getImage());
        }
        viewPager.setCurrentItem(0, false);
    }

    private void addImage(String url) {
        ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.pager_item_property_image, null);
        ImageView imageView = view.findViewById(R.id.image);
        Picasso.with(activity).load(url).fit().centerCrop().into(imageView);
        pagerAdapter.addView(view);
        viewPager.setCurrentItem(pagerAdapter.getCount() - 1, true);
        updatePhotosText();
    }

    public void setProperty(Property property) {
        loading.set(true);
        this.property.setId(property.getId());
        NetworkModule.getInteractor().getProperty(property);
    }

    public void onStarClicked() {
        if (property.getStarred() == null) {
            property.setStarred(starred.get());
        }
        property.setStarred(!property.getStarred());
        starred.set(property.getStarred());

        if (property.getStarred()) {
            NetworkModule.getInteractor().addFavoriteProperty(null, property.getId());
        } else {
            NetworkModule.getInteractor().removeFavoriteProperty(null, property.getId());
        }
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

    private void updatePhotosText() {
        if (property.getPhotos() != null && property.getPhotos().isEmpty()) {
            noImages.set(true);
        } else {
            photosText.set((viewPager.getCurrentItem() + 1) + " of " + property.getPhotos().size());
            noImages.set(false);
        }
    }

    public void onAvatarClick() {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("userId", property.getUser().getUserId());
        activity.startActivity(intent);
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    private void addMarker() {
        if (property == null || map == null) return;
        map.clear();
        Drawable icon = ContextCompat.getDrawable(activity, R.drawable.marker_map_green);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(property.getLatitude(), property.getLongitude()))
                .title("$" + property.getPrice_to_human())
                .snippet("$" + property.getArv_to_human())
                .infoWindowAnchor(0.5F, 0.5F)
                .icon(Utils.getBitmapDescriptor(icon)));
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }
}
