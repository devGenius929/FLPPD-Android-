package com.antigravitystudios.flppd.ui.profile;

import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antigravitystudios.flppd.BR;
import com.antigravitystudios.flppd.BillingManager;
import com.antigravitystudios.flppd.ConnectionItemListener;
import com.antigravitystudios.flppd.ConnectionsListAdapter;
import com.antigravitystudios.flppd.PropertiesPagerAdapter;
import com.antigravitystudios.flppd.PropertyItemListener;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.ConnectionsListReceivedEvent;
import com.antigravitystudios.flppd.events.ProfileReceivedEvent;
import com.antigravitystudios.flppd.events.PropertiesListReceivedEvent;
import com.antigravitystudios.flppd.models.Connection;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.messages.ChatActivity;
import com.antigravitystudios.flppd.ui.property.PropertyActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileViewModel extends BaseActivityViewModel<ProfileActivity> implements PropertyItemListener {

    private Gson gson = new Gson();

    public boolean isMe;
    public int userId;
    public ObservableBoolean loading = new ObservableBoolean();
    public User user = new User();
    public ArrayList<Connection> connections = new ArrayList<>();
    public ArrayList<Property> properties = new ArrayList<>();

    public PropertiesPagerAdapter propertiesPagerAdapter;
    public RecyclerView.Adapter connectionsAdapter;


    public ProfileViewModel(final ProfileActivity activity, int userId) {
        super(activity);
        this.userId = userId;

        isMe = userId == SPreferences.getUser().getUserId();

        propertiesPagerAdapter = new PropertiesPagerAdapter(activity.getSupportFragmentManager(), properties, this);
        connectionsAdapter = new ConnectionsListAdapter(connections, new ConnectionItemListener() {
            @Override
            public void onItemClicked(Connection item) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("userId", item.getUser().getUserId());
                activity.startActivity(intent);
            }
        });

        load();
    }

    private void load() {
        loading.set(true);
        NetworkModule.getInteractor().getUserProfile(SPreferences.getToken(), userId);
        NetworkModule.getInteractor().getProperties(userId, null, null);
        NetworkModule.getInteractor().getUserConnections(SPreferences.getToken(), userId);
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
    public void onProfileReceivedResult(ProfileReceivedEvent event) {
        if (userId != event.getUserId()) return;

        loading.set(false);
        if (event.isSuccessful()) {
            setUser(event.getResult());
        } else {
            activity.showToast(event.getErrorMessage());
        }
    }

    @Bindable
    public User getUser() {
        return user;
    }

    private void setUser(User result) {
        user = result;
        notifyPropertyChanged(BR.user);

        if(SPreferences.getUser().getUserId().equals(user.getUserId())) {
            SPreferences.getUser().notifyChange();
            SPreferences.setUser(user);
        }

        ViewGroup areasView = activity.binding.areasContainer;
        areasView.removeAllViews();

        String areas = result.getAreas();

        if (!TextUtils.isEmpty(areas)) {
            for (String s : areas.split(",")) {
                TextView areaView = (TextView) activity.getLayoutInflater().inflate(R.layout.list_item_area, areasView, false);
                areaView.setText(s);
                areasView.addView(areaView);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionsListReceivedResult(ConnectionsListReceivedEvent event) {
        if (userId != event.getUserId()) return;

        if (event.isSuccessful()) {
            setConnections(event.getResult());
        } else {
            activity.showToast(event.getErrorMessage());
        }
    }

    private void setConnections(List<Connection> result) {
        connections.clear();
        connections.addAll(result);
        connectionsAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertiesListReceivedResult(PropertiesListReceivedEvent event) {
        if (userId != event.getUserId()) return;

        if (event.isSuccessful()) {
            setProperties(event.getResult());
        } else {
            activity.showToast(event.getErrorMessage());
        }
    }

    private void setProperties(List<Property> result) {
        properties.clear();
        properties.addAll(result);
        propertiesPagerAdapter.notifyDataSetChanged();
    }

    public void onSendMessageClick() {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("user", gson.toJson(user));
        activity.startActivity(intent);
    }

    public void onEditClick() {
        activity.startActivityForResult(new Intent(activity, ProfileEditActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            load();
        }
    }

    public void onUpgradeClick() {
        BillingManager.launchSubFlow(activity);
    }

    @Override
    public void onItemClicked(Property property) {
        Intent intent = new Intent(activity, PropertyActivity.class);
        intent.putExtra("property", gson.toJson(property));
        activity.startActivityForResult(intent, 1);
    }

}
