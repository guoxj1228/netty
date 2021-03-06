package com.xj.io.netty.privateprotocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(LoginAuthRespHandler.class);

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    private String[] whiteList = {"127.0.0.1","192.168.0.103"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            //重复登录，拒绝
            if (nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte)-1);
            }else{
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP: whiteList){
                    if (WIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte)0):buildResponse((byte)-1);
                if (isOK){
                    nodeCheck.put(nodeIndex, true);
                }
            }
            logger.info("The login response is : "+loginResp+" body ["+loginResp.getBody()+"]");
            ctx.writeAndFlush(loginResp);

        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }


//    public void channelRead(ChannelHandlerContext ctx, Object msg){
//        NettyMessage message = (NettyMessage) msg;
//        if (message.getHeader() != null && message.getHeader().getType() == (byte)1){
//            System.out.println("Login is OK");
//            String body = (String) message.getBody();
//            System.out.println("Receive message body from client is "+body);
//        }
//        ctx.writeAndFlush(buildLoginResponse((byte)3));
//    }
//
//    private NettyMessage buildLoginResponse(byte result) {
//        NettyMessage message = new NettyMessage();
//        Header header = new Header();
//        header.setType((byte) 2);
//        message.setHeader(header);
//        message.setBody(result);
//        return message;
//    }
//
//    public void channelReadComplete(ChannelHandlerContext ctx){
//        ctx.flush();
//    }
//
//    public void exceptionCaught(ChannelHandlerContext ctx){
//        ctx.close();
//    }
}
