package com.abbsolute.ma_livu.Community;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ImageUpload {

    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public ImageUpload() {
    }

    public ImageUpload(String url) {
        this.url= url;
    }

    public String getUrl() {
        return url;
    }
}