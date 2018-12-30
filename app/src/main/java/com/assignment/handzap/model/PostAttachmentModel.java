package com.assignment.handzap.model;

import android.net.Uri;

public class PostAttachmentModel {
    private Uri imageUri;
    private boolean isVideo;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public PostAttachmentModel(Uri imageUri, boolean isVideo) {
        this.imageUri = imageUri;
        this.isVideo = isVideo;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
