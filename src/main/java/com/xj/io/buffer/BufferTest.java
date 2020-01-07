package com.xj.io.buffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BufferTest {
    public static void main(String[] args) {

//        byteBufferTest();

        intBufferTest();
    }

    private static void intBufferTest() {
        IntBuffer ib = IntBuffer.allocate(5);
        ib.put(1);
        ib.put(2);
        ib.put(3);
        ib.put(4);
        ib.put(5);

        ib.flip();

        for(int i = 0; i < ib.limit();i++){
            System.out.println(ib.get());
        }



    }

    public static void byteBufferTest(){
        ByteBuffer bf = ByteBuffer.allocate(26);
        bf.putInt(1);
        bf.putChar('a');
        bf.putDouble(2.0);
        bf.putFloat(3.0f);
        bf.putLong(4l);

        bf.flip();

        for (int i = 0; i < bf.limit(); i++){
            System.out.println(bf.get());
        }
    }
}
