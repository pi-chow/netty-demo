package com.cetiti.nettyserver.handler.protocol;

import com.cetiti.nettyserver.domain.protocol.Header;
import com.cetiti.nettyserver.domain.protocol.NettyMessage;
import com.cetiti.nettyserver.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端握手与安全认证
 * @author zhouliyu
 * */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>(); //节点验证缓存
    //白名单
    private String[] whiteList = {"127.0.0.1", "10.70.1.120"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //如果是握手请求信息，处理，其他消息透传
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.getType()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            //重复登录，拒绝
            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP : whiteList) {
                    if(WIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK? buildResponse((byte) 0) : buildResponse((byte) -1);
                if(isOK){
                    nodeCheck.put(nodeIndex, isOK);
                }
            }
            System.out.println("the login resp is " + loginResp + " body " + loginResp.getBody());
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);//将消息发送到下一个channel
        }
    }
    private NettyMessage buildResponse(byte result){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.getType());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
