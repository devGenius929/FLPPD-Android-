package com.antigravitystudios.flppd.models.realm;

import com.google.gson.annotations.Expose;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manuel on 28/7/2017.
 */

public class RealmPhoto extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    @Expose
    private String image;

    public RealmPhoto() {
    }

    public RealmPhoto(String image) {
        this.image = image;
    }

    public String getImage() {
            return image;
        }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
            this.id = id;
        }
}
