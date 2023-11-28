package com.example.socket.client.core;

import com.example.socket.common.model.dto.Message;
import com.example.socket.common.model.dto.MessageType;
import com.example.socket.common.model.dto.User;
import com.example.socket.common.util.Receiver;
import com.example.socket.common.util.Sender;
import lombok.Data;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
@Data
public class SocketClient {
    // 当前用户
    private final User user = new User();
    // 接收信息队列
    private final BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();

    private final Sender sender;

    public SocketClient(String name, String ip, int port) throws IOException {
        user.setName(name);
        user.setSocket(new Socket(ip, port));
        sender = new Sender(user.getSocket());
    }

    public void start() {
        // 启动一个接收信息线程
        new Thread(new Receiver(user.getSocket(), receiveQueue)).start();
    }

    public void stop() throws IOException {
        user.getSocket().close();
    }
}
