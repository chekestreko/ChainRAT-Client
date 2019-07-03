package com.nefi.chainrat.networking;

//This is just a data struct for the network

import android.content.Context;
import com.nefi.chainrat.CommandType;

public class Command {
    private CommandType type;

    public Object getObj() {
        return obj;
    }

    private Object obj;

    //region get+set
    public CommandType getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }
    //endregion

    private String[] args;

    public Command(CommandType type, String[] args){
        this.type = type;
        this.args = args;
    }
}
