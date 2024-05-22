package com.votingapp.common;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serial;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;

@Data
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageType messageType;
    private Object message;
    private byte[] checksum;

    public Message(MessageType messageType, Object message) {
        this.messageType = messageType;
        this.message = message;
        this.checksum = calculateChecksum();
    }

    @SneakyThrows
    private byte[] calculateChecksum()  {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(((Serializable) message).toString().getBytes());
        return md.digest();
    }

    public boolean isCorrupted()  {
        byte[] currentChecksum = calculateChecksum();
        return !Arrays.equals(checksum, currentChecksum);
    }
}