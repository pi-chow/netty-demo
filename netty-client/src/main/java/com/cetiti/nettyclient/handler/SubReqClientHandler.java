package com.cetiti.nettyclient.handler;

import com.cetiti.nettyserver.domain.SubscribeReq;
import com.cetiti.nettyserver.domain.SubscribeResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class SubReqClientHandler extends SimpleChannelInboundHandler<SubscribeResp> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++){
            ctx.write(req(i));
        }
        ctx.flush();
    }

    private SubscribeReq req(int i){
        SubscribeReq req = new SubscribeReq();
        req.setAddress("神奇的地址");
        req.setPhoneNumber("138****");
        req.setProductName("netty");
        req.setSubReqID(i);
        req.setUserName("zhouliyu");
        return req;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SubscribeResp subscribeResp) throws Exception {
        System.out.println("receive server resp : " + subscribeResp.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
