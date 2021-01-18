package com.antigravitystudios.flppd.ui.messages;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.BR;
import com.antigravitystudios.flppd.MyApplication;
import com.antigravitystudios.flppd.PubnubHelper;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.Utils;
import com.antigravitystudios.flppd.events.PubNubMessageEvent;
import com.antigravitystudios.flppd.events.PubNubPresenceEvent;
import com.antigravitystudios.flppd.models.ChatMessage;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.models.realm.RealmChatChannel;
import com.antigravitystudios.flppd.models.realm.RealmChatMessage;
import com.antigravitystudios.flppd.ui.ImageActivity;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.filestack.Client;
import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_OK;

public class ChatViewModel extends BaseActivityViewModel<ChatActivity> {

    private Gson gson = new Gson();
    private User user;
    private User anotherUser;
    private int anotherUserId;
    private RealmChatChannel chatChannel;
    private Realm realm;
    private PubNub pubnub;
    private RealmResults<RealmChatMessage> messages;
    private Uri photoURI;
    private String photoUrl;
    private boolean lastTyping;

    private Runnable onImageUploadCallback = new Runnable() {
        @Override
        public void run() {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage_id(UUID.randomUUID().toString());
            chatMessage.setChannel_id(chatChannel.getChannel_id());
            chatMessage.setImage_url(photoUrl);
            chatMessage.setTime(System.currentTimeMillis() * 10000);
            chatMessage.setSender_id(user.getUserId());
            publish(chatMessage);
            photoURI = null;
            photoUrl = null;
            loading.set(false);
        }
    };

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    public MyRealmAdapter listAdapter;

    public ObservableField<String> message = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean here = new ObservableBoolean();
    public ObservableBoolean typing = new ObservableBoolean();
    public ObservableField<String> anotherUserName = new ObservableField<>();

    public ChatViewModel(final ChatActivity activity) {
        super(activity);

        user = SPreferences.getUser();
        realm = Realm.getDefaultInstance();
        pubnub = PubnubHelper.getPubnub();

        message.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            private Timer timer = new Timer();
            private final long DELAY = 2000;

            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(message.get().isEmpty()) return;
                updateTyping(true);
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateTyping(false);
                                    }
                                });
                            }
                        },
                        DELAY
                );
            }
        });
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Override
    protected void onActivityStart() {
        super.onActivityStart();
        MyApplication.getInstance().setCurrentChatChannelId(chatChannel.getChannel_id());
    }

    @Override
    protected void onActivityStop() {
        super.onActivityStop();
        MyApplication.getInstance().setCurrentChatChannelId(null);
    }

    @Override
    protected void onActivityDestroy() {
        super.onActivityDestroy();
        pubnub.unsubscribe()
                .channels(Arrays.asList(chatChannel.getChannel_id()))
                .execute();
        realm.close();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(PubNubMessageEvent event) {
        if (event.getMessage().getChannel().equals(chatChannel.getChannel_id())) {
            ChatMessage message = gson.fromJson(event.getMessage().getMessage(), ChatMessage.class);

            final RealmChatMessage chatMessage = new RealmChatMessage();
            chatMessage.setChannel_id(message.getChannel_id());
            chatMessage.setMe(message.getSender_id().equals(user.getUserId()));
            chatMessage.setImageUrl(message.getImage_url());
            chatMessage.setMessage(message.getMessage());
            chatMessage.setTime(event.getMessage().getTimetoken());

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(chatMessage);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewPresence(PubNubPresenceEvent event) {
        if (!event.getPresence().getUuid().equals(user.getUserId().toString()) && event.getPresence().getChannel().equals(chatChannel.getChannel_id())) {
            switch (event.getPresence().getEvent()) {
                case "state-change":
                    typing.set(event.getPresence().getState().getAsJsonObject().get("isTyping").getAsBoolean());
                    break;
                case "join":
                    here.set(true);
                    break;
                case "leave":
                case "timeout":
                    here.set(false);
                    break;
            }
        }
    }

    private void updateTyping(boolean b) {
        if (lastTyping == b) return;

        lastTyping = b;

        JsonObject state = new JsonObject();
        state.addProperty("isTyping", b);

        pubnub.setPresenceState().
                channels(Arrays.asList(chatChannel.getChannel_id()))
                .state(state)
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(PNSetStateResult result, PNStatus status) {

                    }
                });
    }

    public void setAnotherUser(User anotherUser) {
        this.anotherUser = anotherUser;
    }

    public void setChannelId(int userId) {
        loading.set(true);

        anotherUserId = userId;

        int[] users = new int[]{anotherUserId, user.getUserId()};
        Arrays.sort(users);
        String channelId = "conversation#user_" + users[1] + "-user_" + users[0] + "#conversation";

        chatChannel = realm.where(RealmChatChannel.class).equalTo("channel_id", channelId).findFirst();
        if (chatChannel == null) {
            chatChannel = new RealmChatChannel();
            chatChannel.setChannel_id(channelId);
        }

        pubnub.subscribe()
                .channels(Arrays.asList(chatChannel.getChannel_id()))
                .withPresence()
                .execute();

        pubnub.hereNow()
                .channels(Arrays.asList(chatChannel.getChannel_id()))
                .includeUUIDs(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        for (PNHereNowChannelData pnHereNowChannelData : result.getChannels().values()) {
                            for (PNHereNowOccupantData occupantData : pnHereNowChannelData.getOccupants()) {
                                if(!occupantData.getUuid().equals(user.getUserId().toString())) {
                                    here.set(true);
                                }
                            }
                        }
                    }
                });

        anotherUserName.set((anotherUser == null) ? chatChannel.getUser().getName() : anotherUser.getFirstName() + " " + anotherUser.getLastName());

        messages = realm.where(RealmChatMessage.class).equalTo("channel_id", chatChannel.getChannel_id()).findAll().sort("time", Sort.ASCENDING);
        fetchLatestHistory();

        listAdapter = new MyRealmAdapter(messages);

        messages.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<RealmChatMessage>>() {
            @Override
            public void onChange(RealmResults<RealmChatMessage> realmChatMessages, @Nullable OrderedCollectionChangeSet changeSet) {
                activity.binding.recyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });
    }

    private void fetchLatestHistory() {
        long timeStart = 0;

        RealmChatMessage lastMessage;
        if (messages.size() > 0) {
            lastMessage = messages.sort("time", Sort.DESCENDING).first();
            timeStart = lastMessage.getTime() + 1;
        }

        pubnub.history()
                .channel(chatChannel.getChannel_id())
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

                        final ArrayList<RealmChatMessage> chatMessages = new ArrayList<>();
                        for (PNHistoryItemResult item : result.getMessages()) {

                            ChatMessage message = gson.fromJson(item.getEntry(), ChatMessage.class);

                            RealmChatMessage chatMessage = new RealmChatMessage();
                            chatMessage.setChannel_id(message.getChannel_id());
                            chatMessage.setMe(message.getSender_id().equals(user.getUserId()));
                            chatMessage.setImageUrl(message.getImage_url());
                            chatMessage.setMessage(message.getMessage());
                            chatMessage.setTime(item.getTimetoken());

                            chatMessages.add(chatMessage);
                        }

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(chatMessages);
                            }
                        });

                        loading.set(false);
                    }
                });
    }

    public void onSendClick() {
        Utils.hideKeyboard(activity);
        if (message.get().isEmpty()) return;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage_id(UUID.randomUUID().toString());
        chatMessage.setChannel_id(chatChannel.getChannel_id());
        chatMessage.setMessage(message.get());
        chatMessage.setTime(System.currentTimeMillis() * 10000);
        chatMessage.setSender_id(user.getUserId());

        publish(chatMessage);

        message.set("");
    }

    public void onAttachImageClick() {
        final CharSequence[] items = {"Take photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Send image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    onImageCameraClick();
                } else if (item == 1) {
                    onImageGalleryClick();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void uploadImage(Uri imageUri, Runnable callback) {
        Config config = new Config(activity.getString(R.string.filestack_key));
        Client client = new Client(config);

        StorageOptions options = new StorageOptions.Builder()
                .mimeType("image/jpeg")
                .build();

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);

            int maxSizeDimenPx = 1280;

            if (bm.getWidth() > maxSizeDimenPx || bm.getHeight() > maxSizeDimenPx) {
                if (bm.getWidth() > bm.getHeight()) {
                    int destHeight = bm.getHeight() / (bm.getWidth() / maxSizeDimenPx);
                    bm = Bitmap.createScaledBitmap(bm, maxSizeDimenPx, destHeight, false);
                } else {
                    int destWidth = bm.getWidth() / (bm.getHeight() / maxSizeDimenPx);
                    bm = Bitmap.createScaledBitmap(bm, destWidth, maxSizeDimenPx, false);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] bitmapdata = baos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);


            FileLink file = client.upload(bs, bitmapdata.length, false, options);
            photoUrl = activity.getString(R.string.filestack_domain) + file.getHandle();
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

    private void publish(ChatMessage chatMessage) {
        pubnub.publish()
                .channel(chatChannel.getChannel_id())
                .message(chatMessage)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                    }
                });
        pubnub.publish()
                .channel("user_" + anotherUserId + "_inbound")
                .message(chatMessage)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                    }
                });

        String myName = user.getFirstName() + " " + user.getLastName();
        String text = chatMessage.getMessage() == null ? "image" : chatMessage.getMessage();
        String pn_gcm = "{\"data\" : {\"title\":\"" + myName + "\",\"message\":\"" + text + "\",\"channel\":\"" + chatChannel.getChannel_id() + "\"}}";
        String pn_apns = "{\"aps\" : {\"sound\":\"default\",\"badge\":\"1\",\"alert\":{\"body\":\"" + text + "\",\"title\":\"" + myName + "\"}}}";
        Map<String, Object> payload = new HashMap<>();
        payload.put("pn_gcm", new JsonParser().parse(pn_gcm).getAsJsonObject());
        payload.put("pn_apns", new JsonParser().parse(pn_apns).getAsJsonObject());
        pubnub.publish()
                .channel("user_" + anotherUserId + "_inbound")
                .message(payload)
                .shouldStore(false)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish result.
                    }
                });

        MyApplication.getInstance().updateChatChannel(chatMessage);
    }

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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                loading.set(true);
                new Thread() {
                    @Override
                    public void run() {
                        uploadImage(photoURI, onImageUploadCallback);
                    }
                }.start();
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                loading.set(true);
                new Thread() {
                    @Override
                    public void run() {
                        uploadImage(data.getData(), onImageUploadCallback);
                    }
                }.start();
            }
        }
    }

    public class MyRealmAdapter extends RealmRecyclerViewAdapter<RealmChatMessage, MyRealmAdapter.MyViewHolder> {

        public MyRealmAdapter(OrderedRealmCollection<RealmChatMessage> data) {
            super(data, true);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MyViewHolder(DataBindingUtil.inflate(inflater, viewType, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            RealmChatMessage chatMessage = getData().get(position);

            if (chatMessage.isMe()) {
                if (chatMessage.getMessage() == null) {
                    return R.layout.list_item_chat_image_me;
                } else {
                    return R.layout.list_item_chat_message_me;
                }
            } else {
                if (chatMessage.getMessage() == null) {
                    return R.layout.list_item_chat_image_not_me;
                } else {
                    return R.layout.list_item_chat_message_not_me;
                }
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bind(getData().get(position));
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ViewDataBinding binding;

            MyViewHolder(ViewDataBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RealmChatMessage selected = getData().get(getAdapterPosition());
                        if (selected.getImageUrl() != null) {
                            Intent intent = new Intent(activity, ImageActivity.class);
                            intent.putExtra("href", selected.getImageUrl());
                            activity.startActivity(intent);
                        }
                    }
                });
            }

            public void bind(RealmChatMessage item) {
                binding.setVariable(BR.item, item);
                binding.executePendingBindings();
            }

        }
    }

}
