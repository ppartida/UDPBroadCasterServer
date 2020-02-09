package com.porfiriopartida.network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.util.*;

@Component
@ConditionalOnProperty(name = "udp.mode", havingValue = "2")
class BroadcastUDPServer implements ApplicationThread
{
    private static final Logger logger = LoggerFactory.getLogger(BroadcastUDPServer.class);
    private static final int UUID_LENGTH = 36;

    @Autowired
    private NetworkConfiguration configuration;

    private DatagramSocket serverSocket;

    private Map<String, NetworkClient> connectedUsers;

    @PostConstruct
    public void init() throws SocketException {
        serverSocket = new DatagramSocket(configuration.getPort());
        connectedUsers = new HashMap<String, NetworkClient>();
    }

    public void run()
    {
        try {
            while(true)
            {
                try {
                    byte[] receiveData = new byte[Constants.BYTES];
                    //byte[] sendData = new byte[Constants.BYTES];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String sentence = new String( receivePacket.getData());

                    //If stringUuid is not given then everything else is skipped.
                    String stringUuid = extractUuid(sentence);

                    logger.info(String.format("%s", sentence)); //log vulnerability

                    //connectedUsers is just growing, if this is for a specific amount of users this is ok to allow
                    //reconnects, maybe? but dead users or reconnecteds may create a new one so... let's ignore this
                    // for now.

                    String cleanSentence = cleanSentence(sentence);
                    processMessage(stringUuid, cleanSentence, receivePacket);

                } catch (MessageProcessingException e) {
                    logger.error(e.getMessage(), e);
                    continue;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void processMessage(String uuid, String cleanSentence, DatagramPacket receivePacket) {
        InetAddress IPAddress = receivePacket.getAddress();
        UUID uuid_ = UUID.fromString(uuid);
        switch(cleanSentence){
            case Constants.NEW:
                connectedUsers.clear();
                break;
            case Constants.ADD_ME:
                connectedUsers.put(uuid, NetworkClient.buildFrom(IPAddress, uuid_, receivePacket.getPort()));
                break;
            default:
                broadcast(String.format("%s %s", uuid, cleanSentence.trim()));
                break;
        }
    }

    private void broadcast(String cleanSentence) {
        List<NetworkClient> networkClients = new ArrayList<NetworkClient>(connectedUsers.values());
        for (int i = 0; i < networkClients.size(); i++) {
            NetworkClient networkClient = networkClients.get(i);

            send(cleanSentence, networkClient.getAddress(), networkClient.getPort());
        }
    }

    private String cleanSentence(String sentence) {
        if(StringUtils.isEmpty(sentence)){
            return "";
        }
        String cleanSentence = sentence.substring(UUID_LENGTH + 1);
        if(configuration.isCapitalizeMessages()) {
            cleanSentence = cleanSentence.toUpperCase();
        }

        return cleanSentence.trim();
    }

    private String extractUuid(String sentence) throws MessageProcessingException {
        if(sentence == null){
            throw new MessageProcessingException("Invalid UUID given");
        }

        String result = sentence.substring(0, UUID_LENGTH).trim();

        if(result.length() != UUID_LENGTH){
            throw new MessageProcessingException("Invalid UUID given");
        }

        return result;
    }

    @Override
    public void send(String message, InetAddress address, int port) {
        byte[] sendData = message.getBytes();
        this.send(sendData, address, port);
    }

    private void send(byte[] sendData, InetAddress address, int port) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
