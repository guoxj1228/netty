package com.xj.io.nio.demo01;





import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * @author Howinfun
 * @desc 客户端的Selector，负责客户端的读操作
 * @date 2019/6/21
 */

public class ClientSelectorThread implements Runnable{
    private Selector selector;

    public ClientSelectorThread(Selector selector) {
    }

    @Override
    public void run() {
        try {
            ClientHandler clientHandler = new ClientHandler();
            while(true){
                if (selector.select()>0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        if (key.isReadable()){
                            clientHandler.handleRead(key);
                        }

                        iterator.remove();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
