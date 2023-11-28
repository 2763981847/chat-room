package com.example.socket.common.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author Fu Qiujie
 * @since 2023/11/23
 */
public class ImageUtils {
    public static ImageIcon resizeImageIcon(int maxWidth, int maxHeight, ImageIcon imageIcon, boolean isRatio) {
        //默认值 强制拉伸
        int showWidth = maxWidth;
        int showHeight = maxHeight;

        //等比缩放
        if (isRatio) {
            //获得 原宽和原高
            int oriWidth = imageIcon.getIconWidth();
            int oriHeight = imageIcon.getIconHeight();

            if (1.0 * oriWidth / oriHeight >= 1.0 * maxWidth / maxHeight) {
                //图片比较宽
                showHeight = showWidth * oriHeight / oriWidth;
            } else {
                //图片比较长
                showWidth = showHeight * oriWidth / oriHeight;
            }
        }

        Image scaledInstance = imageIcon.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledInstance);
        return imageIcon;
    }

}
