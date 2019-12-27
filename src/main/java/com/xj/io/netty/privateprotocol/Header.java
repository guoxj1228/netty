package com.xj.io.netty.privateprotocol;

import java.util.HashMap;
import java.util.Map;

public final class Header {

    /**
     * Netty消息校验码（三部分）
     * 1、0xABEF:固定值，表明消息是Netty协议消息，2字节
     * 2、主版本号：1~255,1字节
     * 3、次版本号：1~255,1字节
     * crcCode=0xABEF+主版本号+次版本号
     */
    private int crcCode = 0xabef0101;
    //消息长度，包括消息头，消息体
    private int length;
    //集群节点全局唯一，由会话生成器生成
    private long sessionID;
    /**
     * 0:业务请求消息
     * 1:业务响应消息
     * 2:业务ONE-WAY消息（既是请求又是响应）
     * 3:握手请求消息
     * 4:握手应答消息
     * 5:心跳请求消息
     * 6:心跳应答消息
     */
    private byte type;
    //消息优先级：0~255
    private byte priority;
    //可选，用于扩展消息头
    private Map<String, Object> attachment = new HashMap<>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionID=" + sessionID +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
