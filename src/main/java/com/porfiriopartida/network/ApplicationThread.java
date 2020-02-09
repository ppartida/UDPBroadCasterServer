package com.porfiriopartida.network;

import java.net.InetAddress;

public interface ApplicationThread extends Runnable{
    void send(String message, InetAddress address, int port);
}
