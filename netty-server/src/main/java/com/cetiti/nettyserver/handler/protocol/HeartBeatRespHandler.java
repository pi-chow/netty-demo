package com.cetiti.nettyserver.handler.protocol;

import com.cetiti.nettyserver.domain.protocol.Header;
import com.cetiti.nettyserver.domain.protocol.NettyMessage;
import com.cetiti.nettyserver.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //返回心跳应答
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.getType()){
            System.out.println("receive client heart beat : " + message);
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("send heart beat resp : " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartBeat(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.getType());
        message.setHeader(header);
        return message;
    }


}
