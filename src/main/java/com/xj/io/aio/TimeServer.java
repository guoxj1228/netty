package com.xj.io.aio;

public class TimeServer {
    public static void main(String[] args) {
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(8082);
        new Thread(timeServer,"Aio-TimeServer").start();

    }
}
