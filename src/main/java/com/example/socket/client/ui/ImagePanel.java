package com.example.socket.client.ui;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image image = null;

    public void paintImage(Image image) {
        this.image = image;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}