package com.antigravitystudios.flppd.ui.main;

import android.content.Intent;
import android.databinding.Bindable;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antigravitystudios.flppd.BillingManager;
import com.antigravitystudios.flppd.PubnubHelper;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.models.realm.RealmChatChannel;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.explore.ExploreFragment;
import com.antigravitystudios.flppd.ui.glossary.GlossaryFragment;
import com.antigravitystudios.flppd.ui.home.HomeActivity;
import com.antigravitystudios.flppd.ui.messages.MessagesFragment;
import com.antigravitystudios.flppd.ui.network.NetworkFragment;
import com.antigravitystudios.flppd.ui.profile.ProfileActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushRemoveAllChannelsResult;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainViewModel extends BaseActivityViewModel<MainActivity> {

    public User user;

    public DrawerLayout drawerLayout;
    public NavigationView drawer;
    public ActionBar toolbar;

    private Realm realm;
    private RealmResults<RealmChatChannel> unreadMessages;

    public MainViewModel(MainActivity activity) {
        super(activity);

        PubnubHelper.init();

        if (SPreferences.isJustLogged()) {
            NetworkModule.getInteractor().setpush(SPreferences.getToken(), Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), SPreferences.getPushToken());
            SPreferences.setJustLogged();
        }

        realm = Realm.getDefaultInstance();
        unreadMessages = realm.where(RealmChatChannel.class).equalTo("read", false).findAll();
        unreadMessages.addChangeListener(new RealmChangeListener<RealmResults<RealmChatChannel>>() {
            @Override
            public void onChange(RealmResults<RealmChatChannel> realmChatChannels) {
                updateMessagesCounter();
            }
        });
    }

    @Override
    protected void onActivityStart() {
        super.onActivityStart();
        updateMessagesCounter();
    }

    @Bindable
    public User getUser() {
        user = SPreferences.getUser();
        return user;
    }

    @Override
    protected void registerBus(EventBus bus) {

    }

    @Override
    protected void unregisterBus(EventBus bus) {

    }

    @Override
    protected boolean onActivityBackPressed() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            closeDrawer();
            return true;
        } else if (activity.binding.searchView.isSearchOpen()) {
            activity.binding.searchView.closeSearch();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityDestroy() {
        super.onActivityDestroy();
        PubnubHelper.destroy();
    }

    private void updateMessagesCounter() {
        TextView view = (TextView) ((ViewGroup) drawer.getMenu().findItem(R.id.messages).getActionView()).getChildAt(0);
        int count = unreadMessages.size();
        if (count == 0) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(String.valueOf(count));
        }
    }

    private void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    private void openFragment(Fragment fragment, String title) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        toolbar.setTitle(title);
    }

    public void onExitClick() {

        NetworkModule.getInteractor().logout(SPreferences.getToken());

        PubnubHelper.getPubnub().removeAllPushNotificationsFromDeviceWithPushToken()
                .deviceId(FirebaseInstanceId.getInstance().getToken())
                .pushType(PNPushType.GCM)
                .async(new PNCallback<PNPushRemoveAllChannelsResult>() {
                    @Override
                    public void onResponse(PNPushRemoveAllChannelsResult result, PNStatus status) {

                    }
                });

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
        }

        SPreferences.clearSession();

        Intent i = new Intent(activity, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

    public void onExploreClick() {
        openFragment(ExploreFragment.newInstance("all"), activity.getString(R.string.explore));
        closeDrawer();
    }

    public void onNetworkClick() {
        openFragment(new NetworkFragment(), activity.getString(R.string.network));
        closeDrawer();
    }

    public void onEvaluateClick() {
    }

    public void onMessagesClick() {
        openFragment(new MessagesFragment(), activity.getString(R.string.messages));
        closeDrawer();
    }

    public void onMyListingsClick() {
        openFragment(ExploreFragment.newInstance(SPreferences.getUser()), activity.getString(R.string.my_listings));
        closeDrawer();
    }

    public void onSavedListingsClick() {
        openFragment(ExploreFragment.newInstance("starred"), activity.getString(R.string.saved_listings));
        closeDrawer();
    }

    public void onGlossaryClick() {
        openFragment(new GlossaryFragment(), activity.getString(R.string.glossary));
        closeDrawer();
    }

    public void onHeaderClick() {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("userId", user.getUserId());
        activity.startActivity(intent);
        closeDrawer();
    }

    public void onUpgradeClick() {
        BillingManager.launchSubFlow(activity);
    }
}
