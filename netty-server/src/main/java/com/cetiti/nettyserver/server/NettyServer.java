package com.cetiti.nettyserver.server;

import com.cetiti.nettyserver.codec.MarshallingCodecFactory;
import com.cetiti.nettyserver.codec.protocol.NettyMessageDecoder;
import com.cetiti.nettyserver.codec.protocol.NettyMessageEncoder;
import com.cetiti.nettyserver.handler.SubReqServerHandler;
import com.cetiti.nettyserver.handler.protocol.HeartBeatReqHandler;
import com.cetiti.nettyserver.handler.protocol.HeartBeatRespHandler;
import com.cetiti.nettyserver.handler.protocol.LoginAuthReqHandler;
import com.cetiti.nettyserver.handler.protocol.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {

    public void bind(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyMessageDecoder(1024*1024, 4, 4, -8, 0));
                            socketChannel.pipeline().addLast(new NettyMessageEncoder());
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(50));
                            socketChannel.pipeline().addLast(new LoginAuthRespHandler());
                            //socketChannel.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });
            //绑定端口，同步等待成功
            ChannelFuture future = b.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
        new NettyServer().bind(port);
    }
}
