package com.xj.io.nio;

public class TimeServer {
    public static void main(String[] args) {

        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(8081);
        new Thread(multiplexerTimeServer,"Nio-multiplexerTimeServer-001").start();

    }
}
