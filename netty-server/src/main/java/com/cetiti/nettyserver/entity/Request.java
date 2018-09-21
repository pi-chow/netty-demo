package com.cetiti.nettyserver.entity;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1428751657027721301L;

    private String id;
    private String name;
    private String requestMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", requestMessage='" + requestMessage + '\'' +
                '}';
    }
}
