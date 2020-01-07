package com.xj.io.netty.privateprotocol;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void connect(int port, String host){
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4, 4));
                            ch.pipeline().addLast("messageEncoder", new NettyMessageEncoder());
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(10));
                            ch.pipeline().addLast("loginAuthHandler", new LoginAuthReqHandler());
                            ch.pipeline().addLast("heartBeatHandler", new HeartBeatReqHandler());
                        }
                    });
            //发起异步连接操作

//            ChannelFuture f = b.connect(new InetSocketAddress(host, port),
//                    new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Netty client start ok . remoteAddress("+host+":"+port+"),localAddress("+NettyConstant.LOCALIP+":"+NettyConstant.LOCAL_PORT+")");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        //发起重连操作
                        connect(NettyConstant.REMOTE_PORT,NettyConstant.REMOTEIP);
                        System.out.println("NettyClient 每隔5秒对NettyServer重新发起连接请求");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        new NettyClient().connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTEIP);
    }


//    public void connect(String remoteServer, int port){
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        b.group(workerGroup)
//                .channel(NioSocketChannel.class)
//                .handler(new ChildChannelHandler());
//
//        try {
//            ChannelFuture f = b.connect(remoteServer, port).sync();
//            System.out.println("Netty time client connected at port "+port);
//            f.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//    public static class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
//
//        @Override
//        protected void initChannel(SocketChannel ch) throws Exception {
//            //-8 表示lengthAdjustment，让解码器从0开始截取字节，并且包含消息头
//            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024,4,4,-8,0))
//                    .addLast(new NettyMessageEncoder())
//                    .addLast(new LoginAuthReqHandler());
//        }
//    }
//
//    public static void main(String[] args) {
//        new NettyClient().connect("127.0.0.1",8080);
//    }
}
