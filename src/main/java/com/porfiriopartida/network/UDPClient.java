package com.porfiriopartida.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "udp.mode", havingValue = "1")
class UDPClient implements ApplicationThread
{
    private static final Logger logger = LoggerFactory.getLogger(UDPClient.class);

    @Autowired
    private NetworkConfiguration configuration;

    @Autowired
    private UUID uuid;

    @Autowired
    private DatagramSocket clientSocket;

    private InetAddress IPAddress;

    private boolean running = true;

    @PostConstruct
    public void init() throws UnknownHostException {
        System.setProperty("java.awt.headless", "false");

        IPAddress = InetAddress.getByName(configuration.getHost());
        send("Hello world!");
        new Thread(() -> {
            while(running){
                String s = promptUsersInput();
                if ((s != null) && (s.length() > 0)) {
                    send(s);
                } else if(s == null){
                    break;
                }
            }
            running = false;
            System.exit(0);
        }).start();

    }

    @Override
    public void send(String message, InetAddress address, int port) {
        message = String.format("%s: %s", uuid.toString(), message);
        byte[] sendData = message.getBytes();
        this.send(sendData, address, port);
    }

    public void send(String message) {
        this.send(message, IPAddress, configuration.getPort());
    }

    private void send(byte[] sendData, InetAddress address, int port) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
//            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    private String promptUsersInput() {
        return JOptionPane.showInputDialog(
                null,
                "Send your message",
                String.format("User: %s", uuid),
                JOptionPane.PLAIN_MESSAGE);
    }

    public void run()
    {
        try{
            byte[] receiveData = new byte[Constants.BYTES];
            while(running) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String serverMessage = new String(receivePacket.getData());
                logger.info(String.format("FROM SERVER: %s", serverMessage));
            }
        } catch (
                IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            clientSocket.close();
            System.exit(0);
        }
    }
}

