package com.cetiti.nettyserver.domain.protocol;

public class NettyMessage {
    private Header Header; //消息头
    private Object body; //消息体

    public Header getHeader() {
        return Header;
    }

    public void setHeader(Header Header) {
        this.Header = Header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "Header=" + Header +
                ", body=" + body +
                '}';
    }
}
