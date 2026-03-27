package com.sohaib.cameraview.dev_picture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sohaib.cameraview.helper.CameraLogger;
import com.sohaib.cameraview.helper.PictureResult;

/**
 * Helps with logging.
 */
public abstract class SnapshotPictureRecorder extends PictureRecorder {
    private static final String TAG = SnapshotPictureRecorder.class.getSimpleName();
    protected static final CameraLogger LOG = CameraLogger.create(TAG);

    public SnapshotPictureRecorder(@NonNull PictureResult.Stub stub,
                                   @Nullable PictureResultListener listener) {
        super(stub, listener);
    }
}
