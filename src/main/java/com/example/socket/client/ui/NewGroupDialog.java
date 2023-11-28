package com.example.socket.client.ui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NewGroupDialog extends JFrame {


    @Getter
    List<String> userList;

    @Getter
    List<String> selectedList;
    JTextArea textArea = new JTextArea();
    private final JTextArea inputMessage = new JTextArea("请输入群名:");
    private JTextArea groupName = new JTextArea();
    private JTextArea blank = new JTextArea();
    JButton confirmButton = new JButton("确定");
    JList<Object> list = new JList<>();
    BiConsumer<String, List<String>> onConfirm;

    public NewGroupDialog(List<String> userList, String userName, BiConsumer<String, List<String>> onConfirm) {
        this.onConfirm = onConfirm;
        this.userList = userList;
        this.selectedList = new ArrayList<>(Collections.singletonList(userName));
        this.userList.remove(userName);
        getOnlineUser();
        this.userList.add(0, userName);
        setTextArea();
        JScrollPane scrollPane = new JScrollPane(list);
        textArea.setSize(12, 20);
        list.setFont(new Font("微软雅黑", Font.BOLD, 13));
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String tmp = (String) list.getSelectedValue();
                    if (selectedList.contains(tmp)) {
                        selectedList.remove(tmp);
                    } else {
                        selectedList.add(tmp);
                    }
                    setTextArea();
                }
            }
        });
        confirmButton.addActionListener(e -> {
            if (groupName.getText().isBlank()) {
                JOptionPane.showMessageDialog(null, "群名不能为空", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedList.size() < 2) {
                JOptionPane.showMessageDialog(null, "群人数应当不少于2人", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // todo 发送建群请求
            onConfirm.accept(groupName.getText(), selectedList);
            dispose();
        });

        setLayout(new BorderLayout());
        inputMessage.setEnabled(false);
        inputMessage.setBounds(0, 0, 65, 15);
        groupName.setBounds(65, 0, 100, 15);
        blank.setEnabled(false);
        add(inputMessage);
        add(groupName);
        add(blank, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(textArea, BorderLayout.EAST);
        textArea.setEditable(false);
        add(confirmButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void getOnlineUser() {
        DefaultListModel DModel = new DefaultListModel();        //创建model
        for (int i = 0; i < userList.size(); i++) {
            DModel.addElement(userList.get(i));        //存磁盘名字
        }
        list.setModel(DModel);
    }

    private void setTextArea() {
        StringBuilder tmpStr = new StringBuilder();
        for (String tmp :
                selectedList) {
            tmpStr.append(tmp).append("\n");
        }
        textArea.setText(tmpStr.toString());
    }

}