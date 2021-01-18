package com.antigravitystudios.flppd.ui.messages;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.MyApplication;
import com.antigravitystudios.flppd.PubnubHelper;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.databinding.ListItemMessagesContactBinding;
import com.antigravitystudios.flppd.models.ChatMessage;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.models.realm.RealmChatChannel;
import com.antigravitystudios.flppd.models.realm.RealmUser;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;
import com.antigravitystudios.flppd.ui.profile.ProfileActivity;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import retrofit2.Response;

public class MessagesViewModel extends BaseFragmentViewModel<MessagesFragment> implements MaterialSearchView.OnQueryTextListener {

    private Gson gson = new Gson();
    private User user;
    private Realm realm;
    private RealmResults<RealmChatChannel> channels;

    public ObservableBoolean loading = new ObservableBoolean();

    public MyRealmAdapter listAdapter;

    public MaterialSearchView searchView;

    public MessagesViewModel(MessagesFragment fragment) {
        super(fragment);

        user = SPreferences.getUser();
        realm = Realm.getDefaultInstance();

        channels = realm.where(RealmChatChannel.class).findAll();
        fetchLatestHistory();

        listAdapter = new MyRealmAdapter(channels);
    }

    @Override
    protected void onFragmentDetach() {
        super.onFragmentDetach();
        realm.close();
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
    }

    @Override
    protected void registerBus(EventBus bus) {
    }

    @Override
    protected void unregisterBus(EventBus bus) {
    }

    private void fetchLatestHistory() {
        loading.set(true);

        long timeStart = 0;

        if (channels.size() > 0) {
            timeStart = SPreferences.getMessagesLastSync() + 1;
        }

        PubnubHelper.getPubnub().history()
                .channel("user_" + user.getUserId() + "_inbound")
                .count(100)
                .end(timeStart)
                .includeTimetoken(true)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        if (status.isError()) {
                            loading.set(false);
                            return;
                        }

                        ArrayList<Integer> handledChannels = new ArrayList<>();

                        Collections.reverse(result.getMessages());
                        for (PNHistoryItemResult item : result.getMessages()) {
                            ChatMessage message = gson.fromJson(item.getEntry(), ChatMessage.class);
                            message.setTime(item.getTimetoken());

                            if(message.getChannel_id() == null) continue;

                            if (!handledChannels.contains(message.getSender_id())) {
                                MyApplication.getInstance().updateChatChannel(message);
                                handledChannels.add(message.getSender_id());
                            }
                        }

                        SPreferences.setMessagesLastSync(result.getEndTimetoken());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try (Realm realm = Realm.getDefaultInstance()) {
                                    final RealmResults<RealmUser> usersToUpdate = realm.where(RealmUser.class).isNull("time_updated").findAll();
                                    for (final RealmUser realmUser : usersToUpdate) {
                                        Response<User> userResponse = NetworkModule.getApiService().getUserProfile(SPreferences.getToken(), realmUser.getUser_id()).execute();
                                        if (userResponse.isSuccessful()) {
                                            final User user = userResponse.body();
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    realmUser.setAvatar(user.getAvatar());
                                                    realmUser.setName(user.getFirstName() + " " + user.getLastName());
                                                    realmUser.setTime_updated(System.currentTimeMillis());
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                loading.set(false);
                            }
                        }).start();
                    }
                });
    }

    public void setSearchView(MaterialSearchView searchView, MenuItem item) {
        this.searchView = searchView;
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    private void onAvatarClick(int userId) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("userId", userId);
        activity.startActivity(intent);
    }

    public class MyRealmAdapter extends RealmRecyclerViewAdapter<RealmChatChannel, MyRealmAdapter.MyViewHolder> {

        public MyRealmAdapter(OrderedRealmCollection<RealmChatChannel> data) {
            super(data, true);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MyViewHolder(ListItemMessagesContactBinding.inflate(inflater, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bind(getData().get(position));

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ListItemMessagesContactBinding binding;

            MyViewHolder(ListItemMessagesContactBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, ChatActivity.class);
                        intent.putExtra("user_id", getData().get(getAdapterPosition()).getUser().getUser_id());
                        activity.startActivity(intent);
                    }
                });
                binding.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onAvatarClick(getData().get(position).getUser().getUser_id());
                    }
                });
            }

            public void bind(RealmChatChannel item) {
                binding.setItem(item);
                binding.executePendingBindings();
            }
        }
    }
}
