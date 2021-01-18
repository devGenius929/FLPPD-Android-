package com.antigravitystudios.flppd.ui.messages;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.antigravitystudios.flppd.databinding.ActivityChatBinding;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.ui.base.BaseBindingActivity;
import com.google.gson.Gson;

public class ChatActivity extends BaseBindingActivity<ActivityChatBinding, ChatViewModel> {

    private Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User anotherUser = null;

        int user_id = getIntent().getIntExtra("user_id", 0);

        String anotherUserJson = getIntent().getStringExtra("user");
        if(anotherUserJson != null) {
            anotherUser = gson.fromJson(anotherUserJson, User.class);
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.inputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    viewModel.onSendClick();
                    return true;
                }
                return false;
            }
        });

        binding.inputField.setImeOptions(EditorInfo.IME_ACTION_SEND);
        binding.inputField.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if (anotherUser != null) {
            viewModel.setAnotherUser(anotherUser);
        }
        viewModel.setChannelId(user_id);
    }

    @Override
    protected ChatViewModel addViewModel() {
        return new ChatViewModel(this);
    }

    @Override
    protected ActivityChatBinding getBinding(LayoutInflater inflater) {
        return ActivityChatBinding.inflate(inflater);
    }
}

