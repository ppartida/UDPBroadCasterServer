package com.porfiriopartida.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.UUID;

public class NetworkClient implements Comparable{
    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);
    private InetAddress address;
    private UUID uuid;
    private String name;
    private int port;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object client){
        if(client != null && client instanceof NetworkClient){
            return ((NetworkClient) client).getUuid().equals(this.uuid);
        }
        return false;
    }
    @Override
    public int hashCode(){
        if(this.uuid == null){
            return -1;
        }
        return this.uuid.toString().hashCode();
    }

    public static NetworkClient buildFrom(InetAddress address, UUID uuid, int port){
        NetworkClient client = new NetworkClient();
        client.setAddress(address);
        client.setUuid(uuid);
        client.setPort(port);
        logger.debug(address.getHostAddress());
        logger.debug(uuid.toString());
        logger.debug(String.valueOf(port));
        return client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        return o.hashCode() < this.hashCode() ? 1:0;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
