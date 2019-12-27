package com.xj.io.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {
    private static final String DEFAULT_URL = "/src/com/phei/netty/";

    public void run(final int port, final String url){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("http-decoder", new HttpObjectAggregator(65535));
                        socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                        socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                        socketChannel.pipeline().addLast("fileServerHandler",null); //new HttpFileServerHandler(url));
                    }
                });

        try {
            ChannelFuture future = b.bind("192.168.0.103", port).sync();
            System.out.println("http 文件目录服务器启动， 网址为： http://192.168.0.103:"+port+url);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new HttpFileServer().run(6666,DEFAULT_URL);
    }
}
