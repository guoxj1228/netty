package com.xj.io.netty.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class PerformTestUserInfo {
    public static void main(String[] args) throws IOException {

        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        int loop = 1000000;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        long start = System.currentTimeMillis();
        for (int i=0; i < loop; i++){
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("the jdk serializable cost time is : "+(end - start)+"ms");
        System.out.println("-----------------------------------------------");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long startT = System.currentTimeMillis();
        for (int i=0;i < loop;i++){
            byte[] bytes = info.codeC(buffer);

        }
        long endT = System.currentTimeMillis();
        System.out.println("byte array use time : "+(endT - startT)+"ms");


    }
}
