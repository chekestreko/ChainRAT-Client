package com.nefi.chainrat.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Abstract Picture Taking Service.
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */
public abstract class APictureCapturingService {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    final Context context;
    final CameraManager manager;

    /***
     * constructor.
     *
     * @param activity the activity used to get display manager and the application context
     */
    APictureCapturingService(final Context context) {
        this.context = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /***
     * @return  orientation
     */
    int getOrientation() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            final int rotation = windowManager.getDefaultDisplay().getRotation();
            return ORIENTATIONS.get(rotation);
        } else {
            return ORIENTATIONS.get(0);
        }
    }


    /**
     * starts pictures capturing process.
     *
     * @param listener picture capturing listener
     */
    public abstract void startCapturing(final PictureCapturingListener listener, String cameraID, Size dimensions);
    public abstract String[] getCameraIds() throws CameraAccessException;
    public abstract Size[] getCameraSizes(String cameraID) throws CameraAccessException;
    public abstract void startSingleCapture(final PictureCapturingListener listener, String cameraID, Size dimensions);
    public abstract void stopCapturing();
    public abstract String getIDByLensFacing(int facing) throws CameraAccessException;
}