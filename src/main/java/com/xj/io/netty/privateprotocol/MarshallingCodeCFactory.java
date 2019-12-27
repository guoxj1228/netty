package com.xj.io.netty.privateprotocol;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import org.jboss.marshalling.*;

import java.io.IOException;

public class MarshallingCodeCFactory {

    public static Marshaller buildMarshalling() throws IOException {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        return marshaller;
    }

    public static Unmarshaller buildUnMarshalling() throws IOException {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
        return unmarshaller;

    }

//    public static NettyMarshallingDecoder buildMarshallingDecoder(){
//        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
//        MarshallingConfiguration configuration = new MarshallingConfiguration();
//        configuration.setVersion(5);
//        DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
//        NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider, 1024);
//        return decoder;
//    }
//
//    public static NettyMarshallingEncoder buildMarshallingEncoder(){
//        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
//        MarshallingConfiguration configuration = new MarshallingConfiguration();
//        configuration.setVersion(5);
//        DefaultMarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
//        NettyMarshallingEncoder encoder = new NettyMarshallingEncoder(provider);
//        return encoder;
//    }
}
