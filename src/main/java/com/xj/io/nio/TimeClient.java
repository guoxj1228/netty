package com.xj.io.nio;

public class TimeClient {
    public static void main(String[] args) {

        new Thread(new TimeClientHandle("127.0.0.1",8081)).start();
    }
}
