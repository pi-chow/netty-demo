package com.cetiti.nettyserver.handler.protocol;

import com.cetiti.nettyserver.domain.protocol.Header;
import com.cetiti.nettyserver.domain.protocol.NettyMessage;
import com.cetiti.nettyserver.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端心跳请求
 * 目的：检测链路的可用性，不用携带消息体
 * @author zhouliyu
 * */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //握手成功，主动发送心跳消息
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.getType()){
            heartBeat = ctx.executor().scheduleAtFixedRate(
                  new HeartBeatReqHandler.heartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);//每5秒一次心跳
        } else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.getType()){
            System.out.println("Client receive server heart message : " + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    private class heartBeatTask implements Runnable{
        private final ChannelHandlerContext ctx;

        public heartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("Client send heart beat message to server : " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }

        private NettyMessage buildHeartBeat(){
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.getType());
            message.setHeader(header);
            return message;
        }
    }
}
