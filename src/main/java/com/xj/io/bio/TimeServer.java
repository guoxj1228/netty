package com.xj.io.bio;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6666);
            System.out.println("监听端口：6666");
            Socket accept;

            ExecutorService executorService = Executors.newCachedThreadPool();
            while(true){
                System.out.println("等待客户端链接...");
                accept = serverSocket.accept();
                System.out.println("与客户端建立链接...");
                executorService.execute(new ServerSocketHandler(accept));

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                    System.out.println("ServerSocket 关闭！");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
