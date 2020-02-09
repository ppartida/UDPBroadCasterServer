package com.porfiriopartida.network;

public class MessageProcessingException extends Throwable {
    String message;
    public MessageProcessingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
