package com.xj.io.nio;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;


public class TimeClientHandle implements Runnable{

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host, int port) {
        this.host = host == null? "127.0.0.1" : host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        

    }

    @Override
    public void run() {
        try{
            doConnect();

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        while (!stop){
            try {
                selector.select(1000);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    }catch (Exception e){
                        e.printStackTrace();
                        if (key != null){
                            key.cancel();
                            if (key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            //判断是否连接
            SocketChannel sc = (SocketChannel) key.channel();
            System.out.println("客户端连接状态："+key.isConnectable());
            if (key.isConnectable()){
                if (sc.finishConnect()){
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                }else{
                    System.out.println("连接失败，进程退出！");
                    System.exit(1);//连接失败，进程退出
                }
            }
            if (key.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Now is"+body);
                    this.stop = true;

                }else if(readBytes < 0){
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                }else{
                    //读到零字节，忽略
                }

            }

        }

    }

    private void doConnect() throws IOException {
        //如果直接连接成功，则注册到多路复用器上，发送请求数据，读应答
        if (socketChannel.connect(new InetSocketAddress(host, port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }

    }

    private void doWrite(SocketChannel sc) throws IOException {
        String now = String.valueOf(System.currentTimeMillis());
        ByteBuffer writeBuffer = ByteBuffer.allocate(now.getBytes().length);
        writeBuffer.put(now.getBytes());
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()){
            System.out.println("Send order 2 server succeed.");
        }


    }
}
