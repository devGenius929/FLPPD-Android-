package com.antigravitystudios.flppd.ui.base;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseViewModel extends BaseObservable {

    protected EventBus bus = EventBus.getDefault();

    public BaseViewModel() {
        registerBus(bus);
    }

    protected abstract void registerBus(EventBus bus);

    protected abstract void unregisterBus(EventBus bus);

    protected void destroy() {
        unregisterBus(bus);
    }
}
