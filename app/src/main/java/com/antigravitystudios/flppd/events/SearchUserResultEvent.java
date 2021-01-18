package com.antigravitystudios.flppd.events;

import com.antigravitystudios.flppd.models.User;

import java.util.List;

public class SearchUserResultEvent extends BaseNetworkEvent<List<User>> {

    private String name;

    public SearchUserResultEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
