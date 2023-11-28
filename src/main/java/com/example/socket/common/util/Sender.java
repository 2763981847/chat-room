package com.example.socket.common.util;

import com.example.socket.common.model.dto.Message;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
@Getter
public class Sender {
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;

    public Sender(Socket socket) {
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

