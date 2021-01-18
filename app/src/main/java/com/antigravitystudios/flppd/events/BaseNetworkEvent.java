package com.antigravitystudios.flppd.events;

import java.io.IOException;

import retrofit2.Response;

public abstract class BaseNetworkEvent<T> {

    private Response<T> response;
    private Throwable t;

    public BaseNetworkEvent(Response<T> response) {
        this.response = response;
    }

    public BaseNetworkEvent(Throwable t) {
        this.t = t;
    }

    public BaseNetworkEvent() {
    }

    public boolean isSuccessful() {
        return t == null && response.isSuccessful();
    }

    public T getResult() {
        return response.body();
    }

    public void setResponse(Response<T> response) {
        this.response = response;
    }

    public void setT(Throwable t) {
        this.t = t;
    }

    public String getErrorMessage() {
        if (t != null) {
            return t.getMessage();
        }

        if(!response.isSuccessful()) {
            try {
                return response.errorBody().string();
            } catch (IOException ignored) {
                return "Unknown error";
            }
        }

        return null;
    }
}
