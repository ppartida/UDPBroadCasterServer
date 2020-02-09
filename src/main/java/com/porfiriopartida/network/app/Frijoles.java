package com.porfiriopartida.network.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

@Configuration
public class Frijoles {
    @Bean
    public DatagramSocket datagramSocket() throws SocketException {
        return new DatagramSocket();
    }

    @Bean
    public UUID uuid(){
        return UUID.randomUUID();
    }
}
