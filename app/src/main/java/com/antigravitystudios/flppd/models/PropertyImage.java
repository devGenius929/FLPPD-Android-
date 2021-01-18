package com.antigravitystudios.flppd.models;

import android.net.Uri;

public class PropertyImage {

    private Uri uri;
    private String url;

    public PropertyImage(Uri uri) {
        this.uri = uri;
    }

    public PropertyImage(String url) {
        this.url = url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
