package com.cetiti.nettyserver.handler.protocol;

import com.cetiti.nettyserver.domain.protocol.Header;
import com.cetiti.nettyserver.domain.protocol.NettyMessage;
import com.cetiti.nettyserver.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端握手与安全认证
 * @author zhouliyu
 * */
public class LoginAuthReqHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //如果握手应答消息，需要判断是否认证成功
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.getType()){
            byte loginResult = (byte) message.getBody();
            if(loginResult != 0){
                //握手失败
                ctx.close();
            }else {
                System.out.println("receive from server response : " + msg);
                ctx.fireChannelRead(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    private NettyMessage buildLoginReq(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.getType());
        message.setHeader(header);
        return message;
    }
}
