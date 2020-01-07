package com.xj.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

public class BufTest {
    public static void main(String[] args) {
        testByteBuf();
    }

    public static void testByteBuf(){

        ByteBuf buf = Unpooled.buffer(10);
        ByteBuf buffer = Unpooled.buffer(4);

        for (int i = 0; i < buf.capacity(); i++){
            buf.writeInt(i);

            System.out.println(buf.readInt());

            System.out.println(buf.readableBytes());
            System.out.println(buf.writableBytes());
            System.out.println(buf.readerIndex());
            System.out.println(buf.writerIndex());
        }
    }
}
