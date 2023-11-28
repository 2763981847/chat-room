package com.example.socket.client.ui;

import com.example.socket.client.core.SocketClient;
import com.example.socket.common.constant.CommonConstants;
import com.example.socket.common.constant.ServerConstants;
import com.example.socket.common.model.dto.FileContent;
import com.example.socket.common.model.dto.Message;
import com.example.socket.common.model.dto.MessageType;
import com.example.socket.common.model.dto.ReceiverType;
import com.example.socket.common.util.ImageUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterGraphics;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Fu Qiujie
 * @since 2023/11/22
 */
public class Chat extends JFrame {
    private SocketClient socketClient;

    JLabel chatTitle;
    JPanel theChatPanel;
    JLabel groupMembers;
    JPanel wholePanel;
    String receiver = CommonConstants.EVERYONE;

    Map<String, JTextPane> messageMap = new HashMap<>();
    JTextPane messageArea = new JTextPane();
    List<String> userList = new ArrayList<>();
    JList<String>groupMemberList = new JList<>();

    Map<String, Set<String>> groupMap = new HashMap<>();
    private ReceiverType currentReceiverType = ReceiverType.ALL;
    private JLabel onlineTitle;
    private JLabel groupTitle;
    JList<String> jUserList = new JList<>();
    JList<String> jGroupList = new JList<>();

    JList<String>jStateList = new JList<>();//记录在线状态

//    ClientFileThread fileThread;

    public Chat(SocketClient socketClient) {

// todo        文件传输线程
//        fileThread = new ClientFileThread(username);
//        fileThread.start();

        JTextPane temp = new JTextPane();
        messageMap.put(receiver, temp);
        //信息发送区域
        sendMessagePanel();
        //聊天区域
        chatPanelFiled();
        //在线用户列表
        showOnlineUser();
        // 群组列表
        showGroups();

        setLayout(null);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        setResizable(false);


        //设置窗口标题
        setTitle("ChatWAY   " + socketClient.getUser().getName());
        //设置应用图标
        Image img = Toolkit.getDefaultToolkit().getImage("media/ChatWAYIcon.png");
        this.setIconImage(img);

        //建立socket连接
        this.socketClient = socketClient;
        socketClient.start();
        // 启动消息处理线程
        new Thread(new MessageHandler()).start();
        // todo 发送上线信息
        socketClient.getSender().sendMessage(new Message(MessageType.ENTER,
                socketClient.getUser().getName(),
                ReceiverType.ALL,
                CommonConstants.EVERYONE,
                null));
        switchChat(CommonConstants.EVERYONE);
        refreshChatBoard(socketClient.getUser().getName() + "进入了群聊");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // todo 发送下线信息
                socketClient.getSender()
                        .sendMessage(new Message(MessageType.EXIT,
                                socketClient.getUser().getName(),
                                ReceiverType.ALL,
                                CommonConstants.EVERYONE,
                                null));
                try {
                    socketClient.stop();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void chatPanelFiled() {
        //添加群成员列表
        wholePanel = new JPanel();
        wholePanel.setLayout(new BorderLayout());
        theChatPanel = new JPanel();
        theChatPanel.setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        JScrollPane chatScrollPanel = new JScrollPane(messageArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        theChatPanel.add(chatScrollPanel,BorderLayout.CENTER);
        messageArea.setEditable(false); //更改为false，显示对话框不可编辑
        chatScrollPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 6));

        groupMembers = new JLabel("群成员列表",SwingConstants.CENTER);
        groupMembers.setFont(new Font("微软雅黑", Font.PLAIN, 15));

        JPanel listPanel=new JPanel(new GridLayout(1, 2));
        jStateList.setFont(new Font("微软雅黑",Font.BOLD,13));
        groupMemberList.setFont(new Font("微软雅黑",Font.PLAIN,13));
        titlePanel.setBackground(Color.PINK);
        listPanel.add(groupMemberList);
        listPanel.add(jStateList);

        listPanel.setBounds(600,25,195,330);
        titlePanel.setBounds(600,5,195,20);
        titlePanel.add(groupMembers);



        chatTitle=new JLabel("群聊",SwingConstants.CENTER);
        chatTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        theChatPanel.add(chatTitle,BorderLayout.NORTH);

        theChatPanel.setBackground(new Color(125, 186, 255));
        theChatPanel.setBounds(150,5,645,350);
        wholePanel.setPreferredSize(new Dimension(200,330));
        //theChatPanel.setVisible(false);
        wholePanel.add(listPanel,BorderLayout.CENTER);
        wholePanel.add(titlePanel,BorderLayout.NORTH);
        theChatPanel.add(wholePanel,BorderLayout.EAST);
        add(theChatPanel);
        wholePanel.setVisible(false);

    }

    private void sendMessagePanel() {
        final JPanel panel_south = new JPanel();
        panel_south.setLayout(new BorderLayout());
        panel_south.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 6));

        //消息编辑区域
        JTextArea sendArea = new JTextArea();
        sendArea.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 3));
        sendArea.setBackground(new Color(240, 240, 240));
        panel_south.add(sendArea, BorderLayout.CENTER);

        //发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel_south.add(sendButton, BorderLayout.EAST);

        //功能按钮区域
        JPanel functionButtonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        //全部人
        JButton groupChatButton = new JButton("全部人");
        groupChatButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //发送图片
        JButton pictureButton = new JButton("图片");
        pictureButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //发送文件
        JButton fileButton = new JButton("文件");
        fileButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //新建群聊
        JButton newGroupButton = new JButton("新建群聊");
        newGroupButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        functionButtonPanel.add(groupChatButton);
        functionButtonPanel.add(pictureButton);
        functionButtonPanel.add(fileButton);
        functionButtonPanel.add(newGroupButton);
        functionButtonPanel.setBackground(new Color(255, 255, 255));

        panel_south.add(functionButtonPanel, BorderLayout.WEST);


//        panel_south.setBackground(new Color(125, 186, 255));
        panel_south.setBackground(new Color(255, 255, 255));
        panel_south.setBounds(150, 360, 645, 155);
        add(panel_south);

        //开启群聊
        groupChatButton.addActionListener(e -> {
            receiver = CommonConstants.EVERYONE;
            currentReceiverType = ReceiverType.ALL;
            chatTitle.setText(receiver);
            theChatPanel.setBackground(new Color(125, 186, 255));
            JTextPane temp1 = messageMap.get(receiver);
            messageArea.setStyledDocument(temp1.getStyledDocument());
        });

        //发送文件
        fileButton.addActionListener(e -> showFileChooseDialog());

        //发送图片
        pictureButton.addActionListener(e -> showImageChooseDialog());

        sendButton.addActionListener(e -> {
            String msg = sendArea.getText().trim();
            if (msg.isEmpty()) {
                return;
            }
            Message message = new Message(MessageType.MESSAGE, socketClient.getUser().getName(), currentReceiverType, receiver, msg);
            this.socketClient.getSender().sendMessage(message);
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String content = message.getSender() + "  " + time + "\n" + message.getContent();
            refreshChatBoard(content);
            sendArea.setText("");
        });
        //开启群聊
        newGroupButton.addActionListener(e -> new NewGroupDialog(this.userList, socketClient.getUser().getName(), (groupName, selectedList) -> {
            // todo 发送建群请求
            socketClient.getSender()
                    .sendMessage(new Message(MessageType.NEW_GROUP, socketClient.getUser().getName(), ReceiverType.GROUP, groupName, selectedList));
            groupMap.put(groupName, new HashSet<>(selectedList));
            currentReceiverType = ReceiverType.GROUP;
            switchChat(groupName);
            refreshChatBoard(  "你创建了群聊" + groupName);
            refreshGroups();
        }));

    }

    // “选择文件”调用函数
    private void showFileChooseDialog() {
        // 创建一个默认的文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹
        fileChooser.setCurrentDirectory(new File("C:/"));
        // 设置默认使用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("图像文件", "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件", "txt", "doc", "docs"));
        // 打开文件选择框（线程将被堵塞，知道选择框被关闭）
        int result = fileChooser.showOpenDialog(new JPanel());  // 对话框将会尽量显示在靠近 parent 的中心
        // 点击确定
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取路径
            File file = fileChooser.getSelectedFile();
            String name = file.getName();
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                // todo 发送文件

                byte[] bytes = bufferedInputStream.readAllBytes();
                FileContent fileContent = new FileContent(name, bytes);
                Message message = new Message(MessageType.FILE,
                        socketClient.getUser().getName(),
                        currentReceiverType,
                        receiver,
                        fileContent);
                socketClient.getSender().sendMessage(message);
                refreshChatBoard(message.getSender() + "  " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                        "\n" +
                        "发送了文件   " + fileContent.getFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // “选择图片”调用函数
    private void showImageChooseDialog() {
        // 创建一个默认的文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹
        fileChooser.setCurrentDirectory(new File("C:/"));
        // 禁止选择全部文件选项
        fileChooser.setAcceptAllFileFilterUsed(false);
        // 设置默认使用的文件过滤器（图片过滤器）
        fileChooser.setFileFilter(new FileNameExtensionFilter("图像文件", "jpg", "jpeg", "png", "gif"));
        // 打开文件选择框（线程将被堵塞，知道选择框被关闭）
        int result = fileChooser.showOpenDialog(new JPanel());  // 对话框将会尽量显示在靠近 parent 的中心
        // 点击确定
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取路径
            File file = fileChooser.getSelectedFile();
            String name = file.getName();
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                // todo 发送图片
                byte[] bytes = bufferedInputStream.readAllBytes();
                Message message = new Message(MessageType.IMAGE,
                        socketClient.getUser().getName(),
                        currentReceiverType,
                        receiver,
                        new FileContent(name, bytes));
                socketClient.getSender().sendMessage(message);
                refreshChatBoard(message.getSender() + "  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                refreshChatBoard(new ImageIcon(bytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showGroups() {
        groupTitle = new JLabel("群组列表", SwingConstants.CENTER);
        //获取群组
        refreshGroups();
        //给list添加鼠标点击事件
        jGroupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (jGroupList.getSelectedIndex() != -1) {
                    if (e.getClickCount() == 2) {
                        //双击
                        System.out.println("双击了" + jGroupList.getSelectedValue());
                        currentReceiverType = ReceiverType.GROUP;
                        switchChat(jGroupList.getSelectedValue());
                    }
                }

            }
        });

        jGroupList.setFont(new Font("微软雅黑", Font.BOLD, 13));
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(jGroupList, BorderLayout.CENTER);
        groupTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        listPanel.add(groupTitle, BorderLayout.NORTH);
        listPanel.setBounds(5, 260, 140, 255);
        listPanel.setBackground(new Color(255, 160, 125));
        add(listPanel);
    }

    private void showOnlineUser() {

        onlineTitle = new JLabel("用户列表", SwingConstants.CENTER);
        //获取在线用户
        refreshOnlineUser();
        //给list添加鼠标点击事件
        jUserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (jUserList.getSelectedIndex() != -1) {
                    if (e.getClickCount() == 1) {
                        //单击
                        System.out.println("单击了" + userList.get(jUserList.getSelectedIndex()));
                    } else if (e.getClickCount() == 2) {
                        //双击
                        System.out.println("双击了" + userList.get(jUserList.getSelectedIndex()));
                        if (userList.get(jUserList.getSelectedIndex()).equals(socketClient.getUser().getName())) {
                            System.out.println("无法给自己发消息");
                        } else {
                            wholePanel.setVisible(false);
                            currentReceiverType = ReceiverType.USER;
                            switchChat(userList.get(jUserList.getSelectedIndex()));
                        }

                    }
                }
            }
        });

        jUserList.setFont(new Font("微软雅黑",Font.BOLD,13));
        JPanel listPanel=new JPanel(new BorderLayout());
        listPanel.add(jUserList,BorderLayout.CENTER);



        onlineTitle=new JLabel("用户列表",SwingConstants.CENTER);
        onlineTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        listPanel.add(onlineTitle,BorderLayout.NORTH);


        listPanel.setBounds(5,5,140,255);
        listPanel.setBackground(new Color(255, 160, 125));
        add(listPanel);
    }

    private String resolveChatObject(Message message) {
        ReceiverType receiverType = message.getReceiverType();
        if (ReceiverType.USER == receiverType) {
            return message.getSender();
        }
        if (ReceiverType.GROUP == receiverType) {
            return message.getReceiver();
        }
        return CommonConstants.EVERYONE;
    }

    private void switchChat(String chatObject) {
        this.receiver = chatObject;
        chatTitle.setText(receiver);
        theChatPanel.setBackground(new Color(125, 255, 197));
        JTextPane temp;
        if (messageMap.get(this.receiver) == null) {
            temp = new JTextPane();
            messageMap.put(this.receiver, temp);
        } else {
            temp = messageMap.get(this.receiver);
        }
        messageArea.setStyledDocument(temp.getStyledDocument());
        if (currentReceiverType == ReceiverType.GROUP){
            refreshGroupMemberList(receiver);
        }
    }

    private void refreshGroups() {
        // todo 刷新群组
        groupTitle.setText("群组列表(" + groupMap.size() + ")");
        DefaultListModel<String> DModel = new DefaultListModel();
        for (String s : groupMap.keySet()) {
            DModel.addElement(s);        //存磁盘名字
        }
        jGroupList.setModel(DModel);
    }

    private void refreshOnlineUser() {
        onlineTitle.setText("用户列表(" + userList.size() + ")");
        DefaultListModel<String> DModel = new DefaultListModel();
        for (String s : userList) {
            DModel.addElement(s);        //存磁盘名字
        }
        jUserList.setModel(DModel);
    }

    private void refreshGroupMemberList(String groupName) {
        DefaultListModel<String> DModelMembers = new DefaultListModel();
        DefaultListModel<String>DModelState = new DefaultListModel<>();
        ArrayList<String>onlineName = new ArrayList<>();
        ArrayList<String>offlineName = new ArrayList<>();

        for (String s :groupMap.get(groupName) ) {
            if (userList.contains(s)) {
                onlineName.add(s);
            } else {
                offlineName.add(s);
            }

        }
        for (String tmp:onlineName){
            DModelMembers.addElement(tmp);
            DModelState.addElement("在线");
        }
        for (String tmp:offlineName){
            DModelMembers.addElement(tmp);
            DModelState.addElement("离线");
        }
        groupMemberList.setModel(DModelMembers);
        jStateList.setModel(DModelState);
        wholePanel.setVisible(true);
    }

    private void refreshChatBoard(ImageIcon icon) {
        icon = ImageUtils.resizeImageIcon(theChatPanel.getWidth() / 2, theChatPanel.getHeight() / 2, icon, true);
        JTextPane temp;
        if (messageMap.get(this.receiver) == null) {
            temp = new JTextPane();
            messageMap.put(this.receiver, temp);
        } else {
            temp = messageMap.get(this.receiver);
        }
        temp.setCaretPosition(temp.getDocument().getLength());
        temp.insertIcon(icon);
        // 换行
        try {
            temp.getStyledDocument().insertString(temp.getStyledDocument().getLength(), "\n", null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        messageArea.setStyledDocument(temp.getStyledDocument());
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private void refreshChatBoard(String content) {
        JTextPane temp;
        if (messageMap.get(this.receiver) == null) {
            temp = new JTextPane();
            messageMap.put(this.receiver, temp);
        } else {
            temp = messageMap.get(this.receiver);
        }
        try {
            temp.getStyledDocument().insertString(temp.getStyledDocument().getLength(), content + "\n", null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        messageArea.setStyledDocument(temp.getStyledDocument());
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }


    public static void main(String[] args) throws IOException {
        new Chat(new SocketClient("何3", "127.0.0.1", ServerConstants.PORT));
    }

    public class MessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                while (!socketClient.getUser().getSocket().isClosed()) {
                    Message message = socketClient.getReceiveQueue().take();
                    MessageType messageType = message.getType();
                    if (MessageType.USERS == messageType) {
                        userList.clear();
                        userList.addAll((Collection<? extends String>) message.getContent());
                        refreshOnlineUser();
                    } else if (MessageType.GROUPS == messageType) {
                        groupMap.clear();
                        groupMap.putAll((Map<? extends String, ? extends Set<String>>) message.getContent());
                        refreshGroups();
                    } else if (MessageType.ENTER == messageType) {
                        String name = message.getSender();
                        userList.add(name);
                        refreshOnlineUser();
                        Set<String> groupMembers = groupMap.get(receiver);
                        if (groupMap.containsKey(receiver) &&  groupMap.get(receiver).contains(name)){
                            refreshGroupMemberList(receiver);
                        }
                        message.setContent(name + "进入了群聊");
                        refreshChatBoard(message.getContent().toString());

                        // todo 有用户进入
                    } else if (MessageType.EXIT == messageType) {
                        String name = message.getSender();
                        userList.remove(name);
                        refreshOnlineUser();
                        if (groupMap.containsKey(receiver) &&  groupMap.get(receiver).contains(name)){
                            refreshGroupMemberList(receiver);
                        }

                        // todo 有用户退出
                    } else if (MessageType.NEW_GROUP == messageType) {
                        // todo 有用户建群
                        String groupOwner = message.getSender();
                        String groupName = message.getReceiver();
                        List<String> groupMembers = (List<String>) message.getContent();
                        groupMap.put(groupName, new HashSet<>(groupMembers));
                        refreshGroups();
                        currentReceiverType = ReceiverType.GROUP;
                        switchChat(groupName);
                        refreshChatBoard(groupOwner + "创建了群聊" + groupName);
                    } else if (MessageType.MESSAGE == messageType) {
                        // todo 有用户发送消息
                        String content = message.getContent().toString();
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        content = message.getSender() + "  " + time + "\n" + content;
                        currentReceiverType = message.getReceiverType();
                        switchChat(resolveChatObject(message));
                        refreshChatBoard(content);


                    } else if (MessageType.FILE == messageType) {
                        // 有用户发送文件
                        FileContent fileContent = (FileContent) message.getContent();
                        currentReceiverType = message.getReceiverType();
                        switchChat(resolveChatObject(message));
                        refreshChatBoard(message.getSender() + "  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n"
                                + "发送了文件   " + fileContent.getFileName());
                        int result = JOptionPane.showConfirmDialog(new JPanel(), message.getSender() + "发送了文件   " + fileContent.getFileName() + "，是否接受？", "提示",
                                JOptionPane.YES_NO_OPTION);
                        if (result == 0) {
                            // 保存文件
                            File fileFolder = new File("media/" + socketClient.getUser().getName());
                            fileFolder.mkdir();
                            File file = new File("media/" + socketClient.getUser().getName() + "/" + fileContent.getFileName());
                            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                                bufferedOutputStream.write(fileContent.getContent());
                                bufferedOutputStream.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            refreshChatBoard("文件已保存至" + file.getAbsolutePath());
                        }
                    } else if (MessageType.IMAGE == messageType) {
                        //  有用户发送图片
                        FileContent fileContent = (FileContent) message.getContent();
                        currentReceiverType = message.getReceiverType();
                        switchChat(resolveChatObject(message));
                        refreshChatBoard(message.getSender() + "  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                        ImageIcon imageIcon = new ImageIcon(fileContent.getContent());
                        refreshChatBoard(imageIcon);
                    }
                }

            } catch (
                    InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}