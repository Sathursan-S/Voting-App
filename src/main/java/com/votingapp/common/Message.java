package com.votingapp.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageType messageType;
    private Object message;

    public Message(MessageType messageType, Object message) {
        this.messageType = messageType;
        this.message = message;
    }
}
