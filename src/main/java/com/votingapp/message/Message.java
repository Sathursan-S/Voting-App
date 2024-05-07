package com.votingapp.message;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class Message implements Serializable {
    private Object message;
    private MessageType messageType;
}