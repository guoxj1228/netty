package com.xj.io.netty.privateprotocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {


    public void bind(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 100);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                    ch.pipeline().addLast(new NettyMessageEncoder());
                    ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(10));
                    ch.pipeline().addLast(new LoginAuthRespHandler());
                    ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
                }
            });

            ChannelFuture f = b.bind(NettyConstant.REMOTE_PORT).sync();
            System.out.println("Netty server start ok : "+(NettyConstant.REMOTEIP+":"+NettyConstant.REMOTE_PORT));
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        new NettyServer().bind();
    }




//    public void bind(int port){
//        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
//
//        ServerBootstrap b = new ServerBootstrap();
//        b.group(bossGroup, workerGroup)
//                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_BACKLOG, 1024)
//                .childHandler(new ChildChannelHandler());
//
//        try {
//            ChannelFuture f = b.bind(port).sync();
//            System.out.println("Netty time Server started at port "+port);
//            f.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//    public static class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
//        @Override
//        protected void initChannel(SocketChannel ch) throws Exception {
//            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4, 4, -8, 0))
//                    .addLast(new NettyMessageEncoder())
//                    .addLast(new LoginAuthRespHandler());
//        }
//    }
//
//    public static void main(String[] args) {
//        new NettyServer().bind(8080);
//    }
}
