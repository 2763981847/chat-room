package com.example.socket.server.core;

import com.example.socket.common.constant.ServerConstants;
import com.example.socket.common.model.dto.Message;
import com.example.socket.common.model.dto.MessageType;
import com.example.socket.common.model.dto.ReceiverType;
import com.example.socket.common.model.dto.User;
import com.example.socket.common.util.Receiver;
import com.example.socket.common.util.Sender;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
public class SocketServer {
    private final Set<String> onlineUsers = new HashSet<>();

    private final Map<String, Set<String>> groupMap = new ConcurrentHashMap<>();

    private final BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();

    private ThreadPoolExecutor threadPoolExecutor;

    private ServerSocket server;
    private Consumer<String> logsConsumer = System.out::println;

    private final Map<String, Sender> nameSenderMap = new ConcurrentHashMap<>();
    private final MessageExchange messageExchange = new MessageExchange(receiveQueue, nameSenderMap);

    private final Consumer<List<String>> onOnlineUsersChange;

    public SocketServer(Consumer<List<String>> onOnlineUsersChange, Consumer<String> logsConsumer) {
        this.onOnlineUsersChange = onOnlineUsersChange;
        this.logsConsumer = logsConsumer;
    }

    public void start(int port) throws IOException {
        server = new ServerSocket(port);
        threadPoolExecutor = new ThreadPoolExecutor(16, 16, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        // 启动消息交换机
        threadPoolExecutor.submit(messageExchange);
        // 开始监听连接
        threadPoolExecutor.submit(() -> {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    // 启动一个接收线程
                    threadPoolExecutor.submit(new Receiver(socket, receiveQueue));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void stop() throws IOException {
        server.close();
        threadPoolExecutor.shutdown();
    }

    @AllArgsConstructor
    class MessageExchange implements Runnable {
        private BlockingQueue<Message> receiveQueue;
        private Map<String, Sender> nameSenderMap;

        private void broadcast(Message message) {
            // 发送到除了发送者之外的所有用户
            nameSenderMap.entrySet().parallelStream()
                    .filter(entry -> !entry.getKey().equals(message.getSender()) && !entry.getValue().getSocket().isClosed())
                    .forEach(entry -> entry.getValue().sendMessage(message));
        }

        private void multicast(Message message) {
            String receiver = message.getReceiver();
            // 发送到除了发送者之外的群组内所有用户
            groupMap.get(receiver).parallelStream()
                    .filter(name -> !name.equals(message.getSender()))
                    .map(nameSenderMap::get)
                    .filter(Objects::nonNull)
                    .filter(sender -> !sender.getSocket().isClosed())
                    .forEach(sender -> sender.sendMessage(message));
        }

        private void unicast(Message message) {
            String receiver = message.getReceiver();
            Sender sender = nameSenderMap.get(receiver);
            if (sender != null && !sender.getSocket().isClosed()) {
                sender.sendMessage(message);
            }
        }

        private void onEnter(User user) {
            // 新建一个发送器
            Sender sender = new Sender(user.getSocket());
            // 将用户加入在线列表
            onlineUsers.add(user.getName());
            // 发送当前在线用户列表给该用户
            sender.sendMessage(new Message(MessageType.USERS, ServerConstants.SERVER, ReceiverType.USER, user.getName(), onlineUsers));
            // 发送当前用户所在群组发给该用户
            sender.sendMessage(new Message(MessageType.GROUPS, ServerConstants.SERVER, ReceiverType.USER, user.getName(),
                    groupMap.entrySet().stream().filter(entry -> entry.getValue().contains(user.getName()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));

            // 建立用户与发送器的映射
            nameSenderMap.put(user.getName(), sender);
            // 更新服务器在线用户列表
            onOnlineUsersChange.accept(onlineUsers.stream().sorted().collect(Collectors.toList()));

            logsConsumer.accept(user.getName() + "上线了");
        }

        private void onExit(String name, Socket socket) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 将用户从在线列表中移除
            onlineUsers.remove(name);
            // 关闭发送器
            nameSenderMap.remove(name);
            // 关闭接收器
            // 更新服务器在线用户列表
            onOnlineUsersChange.accept(onlineUsers.stream().sorted().collect(Collectors.toList()));
            logsConsumer.accept(name + "下线了");
        }

        @Override
        public void run() {
            while (!server.isClosed()) {
                Message message;
                try {
                    message = receiveQueue.take();
                    MessageType messageType = message.getType();
                    ReceiverType receiverType = message.getReceiverType();
                    if (MessageType.ENTER == messageType) {
                        onEnter(new User(message.getSender(), (Socket) message.getContent()));
                        message.setContent(null);
                        // 发送用户上线消息给其他用户
                        broadcast(message);
                    } else if (MessageType.EXIT == messageType) {
                        onExit(message.getSender(), (Socket) message.getContent());
                        // 发送用户下线消息给其他用户
                        message.setContent(null);
                        broadcast(message);
                    } else if (MessageType.NEW_GROUP == messageType) {
                        // 创建新群组
                        String groupName = message.getReceiver();
                        List<String> selectedUsers = (List<String>) message.getContent();
                        groupMap.put(groupName, new HashSet<>(selectedUsers));
                        // 发送新群组信息给群内用户
                        multicast(message);
                        logsConsumer.accept(message.getSender() + "创建了群组" + groupName);
                    } else {
                        // 接收人为EVERYONE，代表发送给所有人
                        if (ReceiverType.ALL == receiverType) {
                            // 广播
                            broadcast(message);
                        } else if (ReceiverType.GROUP == receiverType) {
                            // 发送给指定群组
                            multicast(message);
                        } else {
                            // 发送给指定用户
                            unicast(message);
                        }
                        logsConsumer.accept(message.getSender() + "发送了一条" + messageType.toString() + "类型的消息到" + (message.getReceiverType() == ReceiverType.ALL ? "所有人" : message.getReceiver()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
