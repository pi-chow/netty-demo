package com.cetiti.nettyserver.codec.protocol;

import com.cetiti.nettyserver.domain.protocol.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

/**
 * 私有协议消息编码器
 * @author zhouliyu
 * */

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private NettyMarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() {
        marshallingEncoder = JBossMarshallingFactory.buildMarshaller();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg,
                          List<Object> list) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new Exception("the encode message is null");
        }
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(msg.getHeader().getCrcCode());
        buf.writeInt(msg.getHeader().getLength());
        buf.writeLong(msg.getHeader().getSessionID());
        buf.writeByte(msg.getHeader().getType());
        buf.writeByte(msg.getHeader().getPriority());
        buf.writeInt(msg.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            buf.writeInt(keyArray.length);
            buf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(ctx, value, buf);
        }

        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            marshallingEncoder.encode(ctx, msg.getBody(), buf);
        }
        int readableBytes = buf.readableBytes();
        buf.setInt(4, readableBytes);

        list.add(buf);
    }
}
