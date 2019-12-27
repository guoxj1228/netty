package com.xj.io.nio;



import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable{


    private ServerSocketChannel channel;
    private Selector selector;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {

            // 打开一个服务端通道
            channel = ServerSocketChannel.open();
            // 非阻塞
            channel.configureBlocking(false);
            // 监听端口号8080
            channel.socket().bind(new InetSocketAddress(port));
            // 打开一个Selector
            selector = Selector.open();

            // 注册到Selector中，ACCEPT操作
            channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server start in port" + port+",selector:"+selector.isOpen());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {

         while (!stop){
             try {
                if (selector.select() > 0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()){
                        key = it.next();

                        try {
                            handleInput(key);
                            it.remove();
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
                }
                 } catch (IOException e) {
                        e.printStackTrace();

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

    private void handleInput(SelectionKey key) throws IOException{
        if(key.isValid()){
            //处理新接入的请求数据
            if (key.isAcceptable()){
                //Accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                //Add the new connection to the selector
                sc.register(key.selector(),SelectionKey.OP_READ,ByteBuffer.allocate(48));


            }

            if (key.isReadable()){
                //Read the data
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order : "+ body);

                    doWrite(sc , System.currentTimeMillis()+"");
                }


            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);

        }
    }
}
