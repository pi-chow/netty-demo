package com.cetiti.nettyserver.enums;

public enum MessageType {
    LOGIN_REQ((byte) 2), LOGIN_RESP((byte) 3),HEARTBEAT_REQ((byte) 4),HEARTBEAT_RESP((byte) 5);

    private byte type;

    MessageType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
