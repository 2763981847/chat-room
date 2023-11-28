package com.example.socket.client;

import com.example.socket.client.core.SocketClient;
import com.example.socket.client.ui.Chat;
import com.example.socket.client.ui.ImagePanel;
import com.example.socket.client.ui.Register;
import com.example.socket.common.constant.ServerConstants;
import com.example.socket.common.model.dto.User;
import com.example.socket.common.model.entity.LoginUser;
import com.example.socket.common.util.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Fu Qiujie
 * @since 2023/11/21
 */

public class Client extends JFrame {


    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        setTitle("ChatWAY");
        Image img = Toolkit.getDefaultToolkit().getImage("media/ChatWAYIcon.png");
        setIconImage(img);
        setLayout(null);
        setSize(320, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);


        //用户头像面板
        JPanel jpUserHead = new JPanel();
        Image image = Toolkit.getDefaultToolkit().getImage("media/touxiang.png");
        ImageIcon imageIcon = new ImageIcon(image);
        ImagePanel imagePanel = new ImagePanel();
        imagePanel.paintImage(image);
        imagePanel.setBounds(110, 5, 100, 100);
        add(imagePanel);

        //界面标题
        JLabel headTitle = new JLabel("登录", SwingConstants.CENTER);
        headTitle.setFont(new Font("微软雅黑", Font.BOLD, 30));  // 设置文本的字体类型、样式 和 大小
        headTitle.setBounds(110, 100, 100, 50);
        add(headTitle);

        //用户名标题
        JLabel username_label = new JLabel("用户名");
        username_label.setBounds(55, 140, 100, 50);
        username_label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(username_label);

        //输入用户名的文本区域
        JTextField username_field = new JTextField();
        username_field.setBounds(52, 175, 216, 35);
        username_field.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(username_field);

        //密码标题
        JLabel password_label = new JLabel("密码");
        password_label.setBounds(55, 200, 100, 50);
        password_label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(password_label);

        //输入密码的文本区域
        JPasswordField password_field = new JPasswordField();
        password_field.setBounds(52, 235, 216, 35);
        password_field.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(password_field);

        //登录按钮
        JButton login = new JButton("登录");
        login.setBounds(105, 290, 110, 40);
        login.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        login.setBackground(new Color(0, 122, 255));
        add(login);
        //注册按钮
        JButton register = new JButton("注册");
        register.setBounds(105, 340, 110, 40);
        register.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        add(register);

        //APP图标面板
        JPanel appIcon = new JPanel();
        Image appImage = Toolkit.getDefaultToolkit().getImage("media/chatway_icon.png");
        ImageIcon appImageIcon = new ImageIcon(appImage);
        ImagePanel appImagePanel = new ImagePanel();
        appImagePanel.paintImage(appImage);
        appImagePanel.setBounds(135, 405, 50, 25);
        add(appImagePanel);

        setVisible(true);   //设置是否可见

        //登录按钮点击事件
        login.addActionListener(e -> {
            String username = username_field.getText();
            String password = String.valueOf(password_field.getPassword());
            if (username.isEmpty() || password.isEmpty()) {

                ImageIcon icon = new ImageIcon("media/cuowu.png");//图片的大小需要调整到合适程度
                JOptionPane.showMessageDialog(null, "登录失败\n账号或密码不能为空", "提示", JOptionPane.ERROR_MESSAGE, icon);
            }
            LoginUser loginUser = DatabaseUtils.getUserByName(username);
            if (loginUser == null || !password.equals(loginUser.getPassword())) {
                ImageIcon icon = new ImageIcon("media/cuowu.png");//图片的大小需要调整到合适程度

                JOptionPane.showMessageDialog(null, "登录失败\n账号或密码错误", "提示", JOptionPane.ERROR_MESSAGE, icon);
                return;
            }
            User user = new User();
            user.setName(username);
            try {
                // todo 自定义ip
                new Chat(new SocketClient(username, "127.0.0.1", ServerConstants.PORT));
                setVisible(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //注册按钮点击事件
        register.addActionListener(e -> {
            setVisible(false);
            new Register();
        });
    }

}

