package com.cetiti.nettyserver.handler;

import com.cetiti.nettyserver.domain.SubscribeReq;
import com.cetiti.nettyserver.domain.SubscribeResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    private static final String DEFAULT_NAME = "zhouliyu";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        SubscribeReq subscribeReq = (SubscribeReq) msg;
        if(DEFAULT_NAME.equalsIgnoreCase(subscribeReq.getUserName())){
            System.out.println("service received : " + subscribeReq.toString());
            ctx.writeAndFlush(resp(subscribeReq.getSubReqID()));
        }
    }

    private SubscribeResp resp(int subReqID){
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setSubReqID(subReqID);
        subscribeResp.setRespCode(0);
        subscribeResp.setDesc("订阅成功");
        return subscribeResp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
