package com.xj.io.netty.privateprotocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(buildLoginReq());
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage) msg;

        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value){
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0){
                ctx.close();
            }else{
                logger.info("Login is ok : "+message);
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.fireExceptionCaught(cause);
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        ctx.writeAndFlush(buildLoginReq());
//    }
//
//    public void channelRead(ChannelHandlerContext ctx, Object msg){
//        NettyMessage message = (NettyMessage) msg;
//        if (message.getHeader() != null && message.getHeader().getType() == (byte)2){
//            System.out.println("Receive from server response");
//        }
//        ctx.fireChannelRead(msg);
//    }
//
//    private NettyMessage buildLoginReq() {
//        NettyMessage message = new NettyMessage();
//        Header header = new Header();
//        header.setType((byte) 1);
//        message.setHeader(header);
//        message.setBody("It is request");
//        return message;
//    }
//
//    public void channelReadComplete(ChannelHandlerContext ctx){
//        ctx.flush();
//    }
//
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
//        ctx.close();
//    }
}
