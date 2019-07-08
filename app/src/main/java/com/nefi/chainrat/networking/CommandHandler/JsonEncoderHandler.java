package com.nefi.chainrat.networking.CommandHandler;

import android.util.Log;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nefi.chainrat.MainActivity;
import com.nefi.chainrat.MainService;
import com.nefi.chainrat.networking.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class JsonEncoderHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Log.d("network", "JsonEncoderHandler");
        Packet packet = (Packet) msg;

        String sPacket = MainService.serialize(packet, Packet.class);

        ByteBuf bytes = ctx.alloc().buffer(sPacket.length());
        bytes.writeCharSequence(sPacket, Charsets.UTF_8);
        ctx.write(bytes, promise);
    }
}
