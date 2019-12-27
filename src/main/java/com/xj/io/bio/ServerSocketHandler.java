package com.xj.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSocketHandler implements Runnable{

    private Socket accept;

    public ServerSocketHandler(Socket accept) {
        this.accept = accept;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            out = new PrintWriter(accept.getOutputStream(),true);
            String msg;
            while (true){
                System.out.println(Thread.currentThread().getId()+"，开始读数据...");
                msg = in.readLine();
                System.out.println(Thread.currentThread().getId()+"，读到数据...");
                if (msg == null){
                    break;
                }
                System.out.println(Thread.currentThread().getId()+",接收到数据：" + msg);
                long l = System.currentTimeMillis();
                out.println(Thread.currentThread().getId()+",返回数据："+l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null){
                out.close();
            }
            if (accept != null){
                try {
                    accept.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
