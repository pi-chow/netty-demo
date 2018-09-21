package com.cetiti.nettyserver;

import com.cetiti.nettyserver.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
/**
 * 简易服务器
 *
 * */
public class EchoServer {

    private final int port;


    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args){
        int port;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }else {
            port = 8080;
        }
        new EchoServer(port).start();
    }

    private void start(){
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(serverHandler);
                    }
                });
        try {
            ChannelFuture cf = sb.bind().sync();
            System.out.println("服务开始");
            cf.channel().closeFuture().sync();
            System.out.println("服务停止");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
