package com.example.socket.common.util;

import com.example.socket.common.model.dto.Message;
import com.example.socket.common.model.dto.MessageType;
import com.example.socket.common.model.dto.User;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
@Getter
public class Receiver implements Runnable {
    private final Socket socket;
    private final BlockingQueue<Message> queue;

    public Receiver(Socket socket, BlockingQueue<Message> queue) {
        this.socket = socket;
        this.queue = queue;
    }


    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            while (!socket.isClosed()) {
                Message message = (Message) objectInputStream.readObject();
                if (message.getType() == MessageType.ENTER || message.getType() == MessageType.EXIT) {
                    message.setContent(socket);
                }
                queue.put(message);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
