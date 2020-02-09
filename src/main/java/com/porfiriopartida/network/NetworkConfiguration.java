package com.porfiriopartida.network;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "udp.server")
public class NetworkConfiguration {
    private String host;
    private int port;
    private boolean capitalizeMessages;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isCapitalizeMessages() {
        return capitalizeMessages;
    }

    public void setCapitalizeMessages(boolean capitalizeMessages) {
        this.capitalizeMessages = capitalizeMessages;
    }
}
