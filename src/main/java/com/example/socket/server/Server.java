package com.example.socket.server;

import com.example.socket.common.constant.ServerConstants;
import com.example.socket.server.core.SocketServer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */
public class Server extends JFrame {
    private JList<String> onlineUsersJList = new JList<>();
    private Consumer<List<String>> onOnlineUsersChange = (onlineUsers) -> {
        onlineUsersJList.setListData(onlineUsers.toArray(new String[0]));
    };

    //消息显示区域
    private JTextArea logsArea = new JTextArea();
    Consumer<String> logsConsumer = (log) -> {
        logsArea.append(log + "\n");
    };

    private final SocketServer socketServer = new SocketServer(onOnlineUsersChange, logsConsumer);

    public Server() {
        //设置流式布局
        setLayout(new BorderLayout());
        //创建信息显示区的画布并添加到show_area
        JScrollPane panel = new JScrollPane(logsArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置信息显示区标题
        panel.setBorder(new TitledBorder("信息显示区"));
        //布局到中央
        add(panel, BorderLayout.CENTER);
        //设置信息显示区为不可编辑
        logsArea.setEditable(false);


        //创建用于显示用户的画布
        final JPanel panelEast = new JPanel();
        //添加流式布局
        panelEast.setLayout(new BorderLayout());
        //设置标题
        panelEast.setBorder(new TitledBorder("在线用户"));
        //在用户显示区添加show_user
        panelEast.add(new JScrollPane(onlineUsersJList), BorderLayout.CENTER);
        //将显示用户的画布添加到整体布局的右侧
        add(panelEast, BorderLayout.EAST);

        //设置该窗口名
        setTitle("服务器 ");
        //引入图片
        Image img = Toolkit.getDefaultToolkit().getImage("media/ChatWAYIcon.png");
        setIconImage(img);
        //设置窗体大小
        setSize(700, 500);
        //设置窗体位置可移动
        setLocationRelativeTo(null);
        //设置窗体关闭方式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体可见
        setVisible(true);
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();

        server.socketServer.start(ServerConstants.PORT);
        server.logsConsumer.accept("服务器启动成功，端口：" + ServerConstants.PORT + "       " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
