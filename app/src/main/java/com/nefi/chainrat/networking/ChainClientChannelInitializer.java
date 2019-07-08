package com.nefi.chainrat.networking;

import com.nefi.chainrat.networking.CommandHandler.CameraRequestHandler;
import com.nefi.chainrat.networking.CommandHandler.JsonDecoderHandler;
import com.nefi.chainrat.networking.CommandHandler.JsonEncoderHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class ChainClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addFirst(new JsonObjectDecoder()); //Wait for input to be valid json
        pipeline.addLast(new JsonDecoderHandler()); //convert json into Packet object
        pipeline.addLast(new CameraRequestHandler()); //Responds to camera requests.
        //Write
        pipeline.addLast(new JsonEncoderHandler()); //convert Packet object to json and write to wire
    }
}
