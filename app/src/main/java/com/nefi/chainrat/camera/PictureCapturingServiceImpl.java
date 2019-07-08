package com.nefi.chainrat.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PictureCapturingServiceImpl extends APictureCapturingService{

    private static final String TAG = PictureCapturingServiceImpl.class.getSimpleName();
    private HandlerThread handlerThread;
    private Handler backgroundHandler;

    private CameraDevice cameraDevice;
    private ImageReader imageReader;

    private boolean cameraClosed;
    private boolean shouldLoop = false;

    private PictureCapturingListener capturingListener;
    private String currentCameraId;
    private Size imageDimensions;

    private PictureCapturingServiceImpl(final Context context) {
        super(context);
    }

    public static APictureCapturingService getInstance(@NonNull final Context context) {
        return new PictureCapturingServiceImpl(context);
    }

    @Override
    public void startSingleCapture(final PictureCapturingListener listener, String cameraID, Size dimensions){

        if (handlerThread != null) {
            // when last capture meet exceptions, release handlerThread first.
            handlerThread.quitSafely();
        }

        this.capturingListener = listener;
        this.currentCameraId = cameraID;
        this.imageDimensions = dimensions;

        try {
            openCamera(cameraID);
        }catch (Exception ex){
            Log.e(TAG, "Exception occurred in startSingleCapture (Call to openCamera())", ex);
        }
    }


    @Override
    public void startCapturing(final PictureCapturingListener listener, String cameraID, Size dimensions) {
        this.shouldLoop = true;
        startSingleCapture(listener, cameraID, dimensions);
    }

    @Override
    public void stopCapturing(){
        this.shouldLoop = false;
    }

    @Override
    public String[] getCameraIds() throws CameraAccessException {
        try{
            CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] out = cm.getCameraIdList();
            if(out.length > 0){
                return out;
            }else{
                Log.e(TAG, "Device has no camera!");
                System.exit(0);
            }
        }catch (CameraAccessException ex){
            Log.e(TAG,"Exception occurred while accessing the list of cameras", ex);
            throw ex;
        }
        return null;
    }

    @Override
    public String getIDByLensFacing(int facing) throws CameraAccessException {
        CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String[] ids = getCameraIds();
        for(String id : ids){
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(id);
            if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == facing){
                return id;
            }
        }
        return null;
    }

    @Override
    public Size[] getCameraSizes(String cameraID) throws CameraAccessException{
        try{
            CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if(streamConfigurationMap == null){
                throw new CameraAccessException(CameraAccessException.CAMERA_ERROR);
            }

            return streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
        }catch (CameraAccessException ex){
            Log.e(TAG,"Exception occurred while accessing the list of cameras", ex);
            throw ex;
        }
    }

    private void openCamera(String cameraID) {
        Log.d(TAG, "Opening camera: " + cameraID);
        handlerThread = new HandlerThread("CameraHandlerThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
        try {
            manager.openCamera(cameraID, stateCallback, backgroundHandler);
        } catch (final CameraAccessException ex) {
            Log.e(TAG, "Exception occurred while opening camera: " + cameraID, ex);
        }
    }



    private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.i(TAG, "Done taking picture from camera " + cameraDevice.getId());
            closeCamera();
        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imReader) {
            final Image image = imReader.acquireLatestImage();
            final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            final byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            //Trigger the event
            capturingListener.onCaptureDone(bytes);
            image.close();
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraClosed = false;
            Log.d(TAG, "Camera " + camera.getId() + " has opened!");
            cameraDevice = camera;
            Log.i(TAG, "Taking picture from camera: " + camera.getId());

            //Take the picture after some delay. It may resolve getting a black dark photos.#
            int delayMillis = 0;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        takePicture(new Size(300,300));
                    } catch (final CameraAccessException e) {
                        Log.e(TAG, "Exception occurred while taking picture from " + currentCameraId, e);
                    }
                }
            }, delayMillis);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera  " + camera.getId() + " has disconnected!");
            if (cameraDevice != null && !cameraClosed) {
                cameraClosed = true;
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            cameraClosed = true;
            String cameraId = camera.getId();
            Log.d(TAG, "Camera " + cameraId + " has closed!");
            //Call the picture done event here I think
            //capturingListener.onCaptureDoneByte(pictureDataHB);
            //Rerun
            if(shouldLoop){
                openCamera(cameraId);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
            if (cameraDevice != null && !cameraClosed) {
                cameraDevice.close();
            }
        }
    };

    private void takePicture(Size imageDimensions) throws CameraAccessException{
        if (null == cameraDevice) {
            Log.e(TAG, "Camera is null");
            return;
        }

        final ImageReader reader = ImageReader.newInstance(imageDimensions.getWidth(), imageDimensions.getHeight(), ImageFormat.JPEG, 2);

        final List<Surface> outputSurfaces = new ArrayList<>();
        outputSurfaces.add(reader.getSurface());

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, getRange(currentCameraId)); //This line of code is used for adjusting the fps range and fixing the dark preview
        captureBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());

        reader.setOnImageAvailableListener(onImageAvailableListener, null);

        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null);
                        } catch (final CameraAccessException e) {
                            Log.e(TAG, "Exception occurred while accessing camera: " + currentCameraId, e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.e(TAG, "onConfigureFailed()");
                    }
                }
                , backgroundHandler);
    }

    private Range<Integer> getRange(String cameraId) {
        CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics chars = null;
        try {
            chars = mCameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Range<Integer>[] ranges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

        Range<Integer> result = null;

        for (Range<Integer> range : ranges) {
            int upper = range.getUpper();

            // 10 - min range upper for my needs
            if (upper >= 10) {
                if (result == null || upper < result.getUpper().intValue()) {
                    result = range;
                }
            }
        }
        return result;
    }



    private void closeCamera() {
        Log.d(TAG, "Closing camera " + cameraDevice.getId());
        if (null != cameraDevice && !cameraClosed) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

}
