package com.cetiti.nettyclient.server;

import com.cetiti.nettyserver.codec.protocol.NettyMessageDecoder;
import com.cetiti.nettyserver.codec.protocol.NettyMessageEncoder;
import com.cetiti.nettyserver.handler.protocol.HeartBeatReqHandler;
import com.cetiti.nettyserver.handler.protocol.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws InterruptedException {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyMessageDecoder(1024*1024, 4, 4, -8, 0));
                            socketChannel.pipeline().addLast(new NettyMessageEncoder());
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(50));
                            socketChannel.pipeline().addLast(new LoginAuthReqHandler());
                            socketChannel.pipeline().addLast(new HeartBeatReqHandler());

                        }
                    });
            //发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        }finally {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect(port, host);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        if(args != null && args.length > 0){
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        new NettyClient().connect(port, "10.70.1.120");
    }
}
