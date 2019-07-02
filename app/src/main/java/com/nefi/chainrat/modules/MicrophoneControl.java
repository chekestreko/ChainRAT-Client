package com.nefi.chainrat.modules;

import android.util.Log;
import com.nefi.chainrat.CommandType;
import com.nefi.chainrat.IModule;
import com.nefi.chainrat.networking.Command;

public class MicrophoneControl implements IModule {

    @Override
    public String name(){
        return "Mic Controll v1";
    }

    @Override
    public String description(){
        return "Lets you stream pictures and videos";
    }

    @Override
    public CommandType handlerType() {
        return CommandType.CAMERA;
    }

    @Override
    public boolean execute(Command command){
        Log.d("1", "OHdd BEY");
        return true;
    }
}
