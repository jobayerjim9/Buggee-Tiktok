package com.systematics.buggee.Video_Recording;

public interface CameraGrabberListener {
    void onCameraInitialized();

    void onCameraError(String errorMsg);
}
