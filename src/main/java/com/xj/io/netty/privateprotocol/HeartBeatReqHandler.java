package com.xj.io.netty.privateprotocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            heartBeat = ctx.executor()
                    .scheduleAtFixedRate(new HeartBeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            System.err.println("[+"+new Date(System.currentTimeMillis()) +"]"+"Client receive server heart beat message:---> "+message);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx){
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage heatBeat = buildHeatBeat();
            System.out.println("[+"+new Date(System.currentTimeMillis()) +"]"+"Client send heart beat message to server:===>"+heatBeat);
            ctx.writeAndFlush(heatBeat);
        }

        private NettyMessage buildHeatBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        if (heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
