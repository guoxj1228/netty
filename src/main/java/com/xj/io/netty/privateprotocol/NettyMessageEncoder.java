package com.xj.io.netty.privateprotocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.Map;

public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @SuppressWarnings("unused")
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {
        if (null == msg || null == msg.getHeader()) {
            throw new Exception("The encode message is null");
        }
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionID());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment()
                .entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
        }
        key = null;
        keyArray = null;
        value = null;

        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else{
            sendBuf.writeInt(0);
        }
        // 此处需要特别注意
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
    }


//    private NettyMarshallingEncoder marshallingEncoder;
//
//    public NettyMessageEncoder(){
//        marshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();
//    }
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
//        if (msg == null || msg.getHeader() == null){
//            throw new Exception("The encode message is null");
//        }
//
//        ByteBuf sendBuf = Unpooled.buffer();
//        sendBuf.writeInt(msg.getHeader().getCrcCode());
//        sendBuf.writeInt(msg.getHeader().getLength());
//        sendBuf.writeLong(msg.getHeader().getSessionID());
//        sendBuf.writeByte(msg.getHeader().getType());
//        sendBuf.writeByte(msg.getHeader().getPriority());
//        sendBuf.writeInt(msg.getHeader().getAttachment().size());
//
//        String key = null;
//        byte[] keyArray = null;
//        Object value = null;
//        for (Map.Entry<String, Object> param: msg.getHeader().getAttachment().entrySet()){
//            key = param.getKey();
//            keyArray = key.getBytes("UTF-8");
//            sendBuf.writeInt(keyArray.length);
//            sendBuf.writeBytes(keyArray);
//            value = param.getValue();
//            marshallingEncoder.encode(ctx, value, sendBuf);
//        }
//
//        key = null;
//        keyArray = null;
//        value = null;
//        if (msg.getBody() != null){
//            marshallingEncoder.encode(ctx, msg.getBody(), sendBuf);
//        }
//
//        //sendBuf.writeInt(0);
//        //在第四个字节处写入Buffer的长度
//        int readableBytes = sendBuf.readableBytes();
//        sendBuf.setInt(4, readableBytes);
//
//        //把Message添加到List传递到下一个Handler
//        out.add(sendBuf);
//    }
}
