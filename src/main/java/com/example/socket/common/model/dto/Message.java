package com.example.socket.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sound.midi.Receiver;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Oreki
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private MessageType type;
    private String sender;
    private ReceiverType receiverType;
    private String receiver;
    private Object content;
}

